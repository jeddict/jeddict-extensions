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
public class NiddleFile {

    private List<String> file;
    private List<Niddle> niddles;

    public NiddleFile(String... files) {
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
     * @return the niddles
     */
    public List<Niddle> getNiddles() {
        return niddles;
    }

    /**
     * @param niddles the niddles to set
     */
    public void setNiddles(List<Niddle> niddles) {
        this.niddles = niddles;
    }
}
