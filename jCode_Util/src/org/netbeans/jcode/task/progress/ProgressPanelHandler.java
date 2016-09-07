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
package org.netbeans.jcode.task.progress;

import org.netbeans.api.progress.aggregate.ProgressContributor;
import org.netbeans.modules.j2ee.persistence.wizard.fromdb.ProgressPanel;

/**
 *
 * @author Gaurav Gupta
 */
public class ProgressPanelHandler implements ProgressHandler {

    private ProgressContributor progressContributor;
    private ProgressPanel progressPanel;

    public ProgressPanelHandler(ProgressContributor progressContributor, ProgressPanel progressPanel) {
        this.progressContributor = progressContributor;
        this.progressPanel = progressPanel;
    }

    @Override
    public void progress(String message) {
    }

    @Override
    public void append(String message) {
    }

    @Override
    public void start() {
    }

    @Override
    public void start(int step) {
    }

    @Override
    public void progress(String message, int step) {
    }

    @Override
    public void finish() {
    }

    @Override
    public void error(String title, String message) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void warning(String title, String message) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void info(String title, String message) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
