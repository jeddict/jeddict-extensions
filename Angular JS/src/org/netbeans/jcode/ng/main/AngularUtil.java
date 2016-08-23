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
package org.netbeans.jcode.ng.main;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.io.IOUtils;
import org.netbeans.jcode.parser.ejs.FileTypeStream;
import org.netbeans.jcode.task.progress.ProgressHandler;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

/**
 *
 * @author jGauravGupta
 */
public class AngularUtil {
        public static void copyDynamicResource(Consumer<FileTypeStream> parserManager, String inputResource, FileObject webRoot, Function<String, String> pathResolver, ProgressHandler handler) throws IOException {
        InputStream inputStream = Angular1Generator.class.getClassLoader().getResourceAsStream(inputResource);
        try (ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (entry.getName().lastIndexOf('.') == -1) {
                    continue;
                }
                String fileType = entry.getName().substring(entry.getName().lastIndexOf('.') + 1);
                String targetPath = pathResolver.apply(entry.getName());
                if (targetPath == null) {
                    continue;
                }
                handler.progress(targetPath);
                FileObject target = org.openide.filesystems.FileUtil.createData(webRoot, targetPath);
                FileLock lock = target.lock();
                try (OutputStream outputStream = target.getOutputStream(lock)) {
                    parserManager.accept(new FileTypeStream(fileType, zipInputStream, outputStream));
                    zipInputStream.closeEntry();
                } finally {
                    lock.releaseLock();
                }
            }
        } catch(Exception ex){
            Exceptions.printStackTrace(ex);
            System.out.println("InputResource : " + inputResource);
        }
    }

    public static void copyDynamicFile(Consumer<FileTypeStream> parserManager, String inputResource, FileObject webRoot, Function<String, String> pathResolver, ProgressHandler handler) throws IOException {
        try {
            InputStream inputStream = Angular1Generator.class.getClassLoader().getResourceAsStream(inputResource);
            String targetPath = pathResolver.apply(inputResource);
            if (targetPath == null) {
                return;
            }
            String fileType = inputResource.substring(inputResource.lastIndexOf('.') + 1);
            handler.progress(targetPath);
            FileObject target = org.openide.filesystems.FileUtil.createData(webRoot, targetPath);
            FileLock lock = target.lock();
            try (OutputStream outputStream = target.getOutputStream(lock)) {
                parserManager.accept(new FileTypeStream(fileType, inputStream, outputStream));
            } finally {
                lock.releaseLock();
            }
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            System.out.println("InputResource : " + inputResource);
        }   
    }

    public static Map<String, String> getResource(String inputResource) {
        Map<String, String> data = new HashMap<>();
        InputStream inputStream = Angular1Generator.class.getClassLoader().getResourceAsStream(inputResource);
        try (ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (entry.getName().lastIndexOf('.') == -1) {
                    continue;
                }
                StringWriter writer = new StringWriter();
                IOUtils.copy(zipInputStream, writer, StandardCharsets.UTF_8.name());
                String fileName = entry.getName();
                fileName = fileName.substring(fileName.lastIndexOf('/') + 1, fileName.lastIndexOf('.'));
                data.put(fileName, writer.toString());
                zipInputStream.closeEntry();
            }
        } catch(Exception ex){
            Exceptions.printStackTrace(ex);
            System.out.println("InputResource : " + inputResource);
        }
        return data;
    }

}
