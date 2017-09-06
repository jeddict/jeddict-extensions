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
package org.netbeans.jcode.parser.ejs;

import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author jGauravGupta
 */
public class FileTypeStream {

    private final String fileName;
    private final InputStream inputStream;
    private final OutputStream outputStream;

    public FileTypeStream(String fileName, InputStream inputStream, OutputStream outputStream) {
        this.fileName = fileName;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    /**
     * @return the fileName
     */
    public String getFileName() {
        return fileName.substring(fileName.lastIndexOf('/') + 1);
    }
    
    /**
     * @return the fileType
     */
    public String getFileType() {
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }

    /**
     * @return the inputStream
     */
    public InputStream getInputStream() {
        return inputStream;
    }

    /**
     * @return the outputStream
     */
    public OutputStream getOutputStream() {
        return outputStream;
    }

}
