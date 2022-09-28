<#-- Copyright 2010-2013 Coda Hale and Yammer, Inc.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.-->
package ${package};

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import jakarta.inject.Inject;
import jakarta.servlet.AsyncEvent;
import jakarta.servlet.AsyncListener;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.microprofile.metrics.ConcurrentGauge;
import org.eclipse.microprofile.metrics.Meter;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.Timer;

public class InstrumentedFilter implements Filter {

    @Inject
    private  MetricRegistry registry;
    private ConcurrentMap<Integer, Meter> statusCodeMeters;
    private Meter otherMeter;
    private Meter timeoutsMeter;
    private Meter errorsMeter;
    private ConcurrentGauge activeRequests;
    private Timer requestTimer;

    private static final int OK = 200;
    private static final int CREATED = 201;
    private static final int NO_CONTENT = 204;
    private static final int BAD_REQUEST = 400;
    private static final int NOT_FOUND = 404;
    private static final int SERVER_ERROR = 500;

    private static final String INSTRUMENTED_FILTER = "InstrumentedFilter.";
    private static final String RESPONSE_CODES = INSTRUMENTED_FILTER + "responseCodes.";
    private static final String OK_METRICS = RESPONSE_CODES + "ok";
    private static final String CREATED_METRICS = RESPONSE_CODES + "created";
    private static final String NO_CONTENT_METRICS = RESPONSE_CODES + "noContent";
    private static final String BAD_REQUEST_METRICS = RESPONSE_CODES + "badRequest";
    private static final String NOT_FOUND_METRICS = RESPONSE_CODES + "notFound";
    private static final String SERVER_ERROR_METRICS = RESPONSE_CODES + "serverError";
    private static final String OTHER_METRICS = RESPONSE_CODES + "other";
    private static final String TIMEOUTS_METRICS = INSTRUMENTED_FILTER + "timeouts";
    private static final String ERRORS_METRICS = INSTRUMENTED_FILTER + "errors";
    private static final String ACTIVE_REQUESTS_METRICS = INSTRUMENTED_FILTER + "activeRequests";
    private static final String REQUESTS_METRICS = INSTRUMENTED_FILTER + "requests";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        statusCodeMeters = new ConcurrentHashMap<>();
        statusCodeMeters.put(OK, registry.meter(OK_METRICS));
        statusCodeMeters.put(CREATED, registry.meter(CREATED_METRICS));
        statusCodeMeters.put(NO_CONTENT, registry.meter(NO_CONTENT_METRICS));
        statusCodeMeters.put(BAD_REQUEST, registry.meter(BAD_REQUEST_METRICS));
        statusCodeMeters.put(NOT_FOUND, registry.meter(NOT_FOUND_METRICS));
        statusCodeMeters.put(SERVER_ERROR, registry.meter(SERVER_ERROR_METRICS));
        otherMeter = registry.meter(OTHER_METRICS);
        timeoutsMeter = registry.meter(TIMEOUTS_METRICS);
        errorsMeter = registry.meter(ERRORS_METRICS);
        activeRequests = registry.concurrentGauge(ACTIVE_REQUESTS_METRICS);
        requestTimer = registry.timer(REQUESTS_METRICS);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        activeRequests.inc();
        final Timer.Context context = requestTimer.time();
        boolean error = false;
        try {
            chain.doFilter(request, response);
        } catch (IOException | RuntimeException | ServletException e) {
            error = true;
            throw e;
        } finally {
            if (!error && request.isAsyncStarted()) {
                request.getAsyncContext().addListener(new AsyncResultListener(context));
            } else {
                context.stop();
                activeRequests.dec();
                if (error) {
                    errorsMeter.mark();
                } else {
                    markStatusCodeMeter(((HttpServletResponse) response).getStatus());
                }
            }
        }
    }

    private void markStatusCodeMeter(int status) {
        final Meter metric = statusCodeMeters.get(status);
        if (metric != null) {
            metric.mark();
        } else {
            otherMeter.mark();
        }
    }

    @Override
    public void destroy() {
    }

    private class AsyncResultListener implements AsyncListener {
        private final Timer.Context context;
        private boolean done = false;

        public AsyncResultListener(Timer.Context context) {
            this.context = context;
        }

        @Override
        public void onComplete(AsyncEvent event) throws IOException {
            if (!done) {
                HttpServletResponse response = (HttpServletResponse) event.getSuppliedResponse();
                context.stop();
                activeRequests.dec();
                markStatusCodeMeter(response.getStatus());
            }
        }

        @Override
        public void onTimeout(AsyncEvent event) throws IOException {
            context.stop();
            activeRequests.dec();
            timeoutsMeter.mark();
            done = true;
        }

        @Override
        public void onError(AsyncEvent event) throws IOException {
            context.stop();
            activeRequests.dec();
            errorsMeter.mark();
            done = true;
        }

        @Override
        public void onStartAsync(AsyncEvent event) throws IOException {
        }
    }

}
