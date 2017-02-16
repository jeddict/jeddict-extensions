/**
 * Copyright [2017] Gaurav Gupta
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
package org.netbeans.jcode.ng.main.domain;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author jGauravGupta
 */
public class NeedleFile {

    private List<String> file;
    private List<Needle> needles;

    public NeedleFile(String... files) {
        if(files==null){
            throw new IllegalStateException("File can not be empty");
        }
        this.file = Arrays.asList(files);
    }
    
    /**
     * @return the file
     */
    public List<String> getFile() {
        return file;
    }

    /**
     * @return the needles
     */
    public List<Needle> getNeedles() {
        return needles;
    }

    /**
     * @param needles the needles to set
     */
    public void setNeedles(List<Needle> needles) {
        this.needles = needles;
    }
}
