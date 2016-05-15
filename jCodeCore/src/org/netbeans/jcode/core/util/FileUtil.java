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

import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.netbeans.api.queries.FileEncodingQuery;
import static org.netbeans.jcode.core.util.JavaSourceHelper.reformat;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;

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

    
    public static void expandTemplate(String inputTemplatePath,  FileObject toDir, String toFile, Map<String, Object> params) throws IOException {

        InputStream contentStream = org.netbeans.jcode.core.util.FileUtil.loadResource(inputTemplatePath);

        FileObject outputFile = toDir.getFileObject(toFile);
        if (outputFile == null) {
            outputFile = org.openide.filesystems.FileUtil.createData(toDir, toFile);
        }
        expandTemplate(contentStream, params, outputFile);
    }
    
     public static void expandTemplate(String inputTemplatePath,  FileObject toFile, Map<String, Object> params) throws IOException {
        InputStream contentStream = org.netbeans.jcode.core.util.FileUtil.loadResource(inputTemplatePath);
        expandTemplate(contentStream, params, toFile);
    }
    
    private static void expandTemplate(InputStream template, Map<String, Object> values, FileObject target) throws IOException {
        Charset targetEncoding = FileEncodingQuery.getEncoding(target);
        FileLock lock = target.lock();
        Writer w = new OutputStreamWriter(target.getOutputStream(lock), targetEncoding);
        try {
            expandTemplate(template, values, targetEncoding, w);
        } finally {
            w.close();
            lock.releaseLock();
        }
        DataObject dob = DataObject.find(target);
        if (dob != null) {
            reformat(dob);
        }
    }

    private static void expandTemplate(InputStream template, Map<String, Object> values, Charset targetEncoding, Writer w) throws IOException {
//        Charset sourceEnc = FileEncodingQuery.getEncoding(template);
        ScriptEngine eng = getScriptEngine();
        Bindings bind = eng.getContext().getBindings(ScriptContext.ENGINE_SCOPE);
        bind.putAll(values);
        bind.put(ENCODING_PROPERTY_NAME, targetEncoding.name());

        Reader is = null;
        try {
            eng.getContext().setWriter(w);
            is = new InputStreamReader(template);
            eng.eval(is);
        } catch (ScriptException ex) {
            throw new IOException(ex);
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }
    
    public static String expandTemplate(String template, Map<String, Object> values){
        StringWriter writer= new StringWriter();
         ScriptEngine eng = getScriptEngine();
        Bindings bind = eng.getContext().getBindings(ScriptContext.ENGINE_SCOPE);
        if(values!=null){
        bind.putAll(values);
        }
        bind.put(ENCODING_PROPERTY_NAME, Charset.defaultCharset().name());
        Reader is = null;
            eng.getContext().setWriter(writer);
            is = new StringReader(template);
        try {
            eng.eval(is);
        } catch (ScriptException ex) {
            Exceptions.printStackTrace(ex);
        }
        
        return writer.toString();
       
    }
    private static final String ENCODING_PROPERTY_NAME = "encoding"; //NOI18N
    private static ScriptEngineManager manager;

    /**
     * Used core method for getting {@code ScriptEngine} from {@code
     * org.netbeans.modules.templates.ScriptingCreateFromTemplateHandler}.
     */
    private static ScriptEngine getScriptEngine() {
        if (manager == null) {
            synchronized (FileUtil.class) {
                if (manager == null) {
                    ClassLoader loader = Lookup.getDefault().lookup(ClassLoader.class);
                    try {
                        loader.loadClass(PrefixResolver.class.getName());
                    } catch (ClassNotFoundException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                    manager = new ScriptEngineManager(loader != null ? loader : Thread.currentThread().getContextClassLoader());
                }
            }
        }
        return manager.getEngineByName((String) "freemarker");
    }

    public static FileObject createFolder(FileObject folder, String name) throws IOException {
        return org.openide.filesystems.FileUtil.createFolder(folder,name);
    }
            
    public static FileObject copyFile(String fromFile, FileObject toDir, String toFile)  throws IOException{
        MakeFileCopy action = new MakeFileCopy(fromFile, toDir, toFile);
        org.openide.filesystems.FileUtil.runAtomicAction(action);
        if (action.getException() != null)
            throw action.getException();
        else
            return action.getResult();
    }
    private static class MakeFileCopy implements Runnable {
        private String fromFile;
        private FileObject toDir;
        private String toFile;
        private IOException exception;
        private FileObject result;

       MakeFileCopy(String fromFile, FileObject toDir, String toFile) {
            this.fromFile = fromFile;
            this.toDir = toDir;
            this.toFile = toFile;
        }

        IOException getException() {
            return exception;
        }

        FileObject getResult() {
            return result;
        }

        public void run() {
            try {
//                if (toDir.getFileObject(toFile) != null) {
//                    return; 
//                }
                FileObject xml = org.openide.filesystems.FileUtil.createData(toDir, toFile);
                String content = readResource(loadResource(fromFile));
                if (content != null) {
                    FileLock lock = xml.lock();
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(xml.getOutputStream(lock)));
                    try {
                        bw.write(content);
                    } finally {
                        bw.close();
                        lock.releaseLock();
                    }
                }
                result = xml;
            }
            catch (IOException e) {
                exception = e;
            }
        }

        private String readResource(InputStream is) throws IOException {
            StringBuilder sb = new StringBuilder();
            String lineSep = System.getProperty("line.separator"); // NOI18N
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            try {
                String line = br.readLine();
                while (line != null) {
                    sb.append(line);
                    sb.append(lineSep);
                    line = br.readLine();
                }
            } finally {
                br.close();
            }
            return sb.toString();
        }
    }

  
}
