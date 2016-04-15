/**
 * Copyright [2016] Gaurav Gupta
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.netbeans.jcode.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Logger;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.Utilities;

/**
 *
 * @author Gaurav Gupta
 */
public class FileUtil {

    public static InputStream loadResource(String resource) {
        InputStream inputStream = null;
        try {
            String n;
            if (resource.startsWith("/")) { // NOI18N
                n = resource.substring(1);
            } else {
                n = resource;
            }

            java.net.URL url = getLoader().getResource(n);
            inputStream = url.openStream();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return inputStream;
    }
    private static volatile Object currentLoader;
    private static Lookup.Result<ClassLoader> loaderQuery = null;
    private static boolean noLoaderWarned = false;
    private static final Logger ERR = Logger.getLogger(ImageUtilities.class.getName());

    private static ClassLoader getLoader() {
        Object is = currentLoader;
        if (is instanceof ClassLoader) {
            return (ClassLoader) is;
        }

        currentLoader = Thread.currentThread();

        if (loaderQuery == null) {
            loaderQuery = Lookup.getDefault().lookup(new Lookup.Template<ClassLoader>(ClassLoader.class));
            loaderQuery.addLookupListener(
                    new LookupListener() {
                        @Override
                        public void resultChanged(LookupEvent ev) {
                            ERR.fine("Loader cleared"); // NOI18N
                            currentLoader = null;
                        }
                    });
        }

        Iterator it = loaderQuery.allInstances().iterator();
        if (it.hasNext()) {
            ClassLoader toReturn = (ClassLoader) it.next();
            if (currentLoader == Thread.currentThread()) {
                currentLoader = toReturn;
            }
            ERR.fine("Loader computed: " + currentLoader); // NOI18N
            return toReturn;
        } else {
            if (!noLoaderWarned) {
                noLoaderWarned = true;
                ERR.warning(
                        "No ClassLoader instance found in " + Lookup.getDefault() // NOI18N
                );
            }
            return null;
        }
    }

    public static boolean isValidUri(String uri) {
        StringTokenizer segments = new StringTokenizer(uri, "/ "); //NOI18N
        Set<String> uriParts = new HashSet<String>();
        while (segments.hasMoreTokens()) {
            String segment = segments.nextToken();
            if (segment.startsWith("{")) { //NOI18N
                if (segment.length() > 2 && segment.endsWith("}")) { //NOI18N
                    String uriPart = segment.substring(1, segment.length() - 1);
                    if (!Utilities.isJavaIdentifier(uriPart)) {
                        return false;
                    }
                    if (uriParts.contains(uriPart)) {
                        return false;
                    } else {
                        uriParts.add(uriPart);
                    }

                } else {
                    return false;
                }
            } else {
                if (segment.contains("{") || segment.contains("}")) { //NOI18N
                    return false;
                }
            }
        }
        return true;
    }

}
