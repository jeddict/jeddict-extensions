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
package org.netbeans.jcode.generator;

import java.io.IOException;
import java.util.Set;
import org.netbeans.jcode.stack.config.data.ApplicationConfigData;
import org.netbeans.jcode.task.progress.ProgressHandler;
import org.openide.filesystems.FileObject;

public abstract class AbstractGenerator {

    private ProgressHandler handler;
    private int totalWorkUnits;
    private int workUnits; 

    public AbstractGenerator() {
    }

    public abstract Set<FileObject> generate(ApplicationConfigData applicationConfigData, ProgressHandler handler) throws IOException;

    protected void initProgressReporting(ProgressHandler handler) {
        initProgressReporting(handler, true);
    }

    protected void initProgressReporting(ProgressHandler handler, boolean start) {
        this.handler = handler;
        this.totalWorkUnits = getTotalWorkUnits();
        this.workUnits = 0;

        if (start) {
            if (totalWorkUnits > 0) {
                handler.start(totalWorkUnits);
            } else {
                handler.start();
            }
        }
    }

    protected void reportProgress(String message) {
        if (handler != null) {
            if (totalWorkUnits > 0) {
                handler.progress(message, ++workUnits);
            } else {
                handler.progress(message);
            }
        }
    }

    protected void finishProgressReporting() {
        if (handler != null) {
            handler.finish();
        }
    }

    protected int getTotalWorkUnits() {
        return 100;
    }

    protected ProgressHandler getProgressHandle() {
        return handler;
    }
}
