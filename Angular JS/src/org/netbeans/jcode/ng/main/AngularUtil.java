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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.io.IOUtils;
import org.netbeans.jcode.console.Console;
import static org.netbeans.jcode.console.Console.FG_BLUE;
import static org.netbeans.jcode.console.Console.FG_RED;
import org.netbeans.jcode.parser.ejs.FileTypeStream;
import org.netbeans.jcode.task.progress.ProgressHandler;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

/**
 *
 * @author jGauravGupta
 */
public class AngularUtil {
        public static void copyDynamicResource(Consumer<FileTypeStream> parserManager, String inputResource, FileObject webRoot, Function<String, String> pathResolver, ProgressHandler handler) throws IOException {
        InputStream inputStream = AngularGenerator.class.getClassLoader().getResourceAsStream(inputResource);
        try (ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (entry.getName().lastIndexOf('.') == -1) {
                    continue;
                }
                String targetPath = pathResolver.apply(entry.getName());
                if (targetPath == null) {
                    continue;
                }
                handler.progress(targetPath);
                FileObject target = org.openide.filesystems.FileUtil.createData(webRoot, targetPath);
                FileLock lock = target.lock();
                try (OutputStream outputStream = target.getOutputStream(lock)) {
                    parserManager.accept(new FileTypeStream(entry.getName(), zipInputStream, outputStream));
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
        
       public static void insertNeedle(FileObject webRoot, String source, String needlePointer, String needleContent, ProgressHandler handler) {
           FileInputStream fis = null;
           if(source.endsWith("json")){
               needlePointer = "\"" + needlePointer + "\"";
           } else {
               needlePointer = " " + needlePointer + " ";
           } 
            try {
                // temp file
                File outFile = File.createTempFile("$$$$$$$", "tmp");
                // input
                FileObject sourceFileObject = webRoot.getFileObject(source);
                if(sourceFileObject==null){
                    handler.error("Needle file", String.format("needle file '%s' not found ", source));
                    return;
                }
                File sourceFile = FileUtil.toFile(sourceFileObject);
                fis = new FileInputStream(sourceFile);
                BufferedReader in = new BufferedReader(new InputStreamReader(fis));
                // output
                PrintWriter out = new PrintWriter(new FileOutputStream(outFile));
                boolean contentUpdated = false;
                String thisLine;
                while ((thisLine = in.readLine()) != null) {
                    if(thisLine.contains(needlePointer)) {
                        out.println(needleContent);
                        contentUpdated = true;
                        System.out.println("needlePointer : " + needlePointer );            
                        System.out.println("needleContent : " + needleContent );            
                    } 
                    out.println(thisLine);
                }
                out.flush();
                out.close();
                in.close();
                if (contentUpdated) {
                    sourceFile.delete();
                    outFile.renameTo(sourceFile);
                } else {
                    outFile.delete();
                }
            } catch (FileNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            } finally {
                try {
                    if(fis!=null)  fis.close();
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
    }

    public static void copyDynamicFile(Consumer<FileTypeStream> parserManager, String inputResource, FileObject webRoot, String targetFile, ProgressHandler handler) throws IOException {
        try {
            InputStream inputStream = AngularGenerator.class.getClassLoader().getResourceAsStream(inputResource);
            handler.progress(targetFile);
            FileObject target = org.openide.filesystems.FileUtil.createData(webRoot, targetFile);
            FileLock lock = target.lock();
            try (OutputStream outputStream = target.getOutputStream(lock)) {
                parserManager.accept(new FileTypeStream(inputResource, inputStream, outputStream));
            } finally {
                lock.releaseLock();
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }   
    }

    public static Map<String, String> getResource(String inputResource) {
        Map<String, String> data = new HashMap<>();
        InputStream inputStream = AngularGenerator.class.getClassLoader().getResourceAsStream(inputResource);
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

    public static void executeCommand(FileObject workingFolder, ProgressHandler handler, String... command){
            try {
                ProcessBuilder pb = new ProcessBuilder(command);
                
//                Map<String, String> env = pb.environment();
                // If you want clean environment, call env.clear() first
//                env.put("VAR1", "myValue");
//                env.remove("OTHERVAR");
//                env.put("VAR2", env.get("VAR1") + "suffix");
                
                pb.directory(FileUtil.toFile(workingFolder));
                Process proc = pb.start();
                BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
                BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
                
                // read the output from the command
                String s;
                while ((s = stdInput.readLine()) != null)
                {
                    handler.append(Console.wrap(s, FG_BLUE));
                }
                
                // read any errors from the attempted command
                while ((s = stdError.readLine()) != null)
                {
                    handler.append(Console.wrap(s, FG_RED));
                }       
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
    }
}
