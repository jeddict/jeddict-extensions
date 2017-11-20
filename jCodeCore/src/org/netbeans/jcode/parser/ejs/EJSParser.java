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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipInputStream;
import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.apache.commons.io.IOUtils;
import org.openide.util.Exceptions;

public final class EJSParser {

    private ScriptEngine engine;
    private CompiledScript cscript;
    private Bindings bindings;
    
    private Character delimiter;
    private Map<String, String> importTemplate;

    public EJSParser() {

        try {
            engine = new NashornScriptEngineFactory().getScriptEngine("--language=es6");
//            engine = new ScriptEngineManager().getEngineByName("nashorn");
            engine.eval("var window = this;"
                    + "var console = {\n"
                    + "  debug: print,\n"
                    + "  warn: print,\n"
                    + "  log: print\n"
                    + "};"
                    + "function contains(obj, a) {\n"
                    + "    for (var i = 0; i < a.length; i++) {\n"
                    + "        if (a[i] === obj) {\n"
                    + "            return true;\n"
                    + "        }\n"
                    + "    }\n"
                    + "    return false;\n"
                    + "}");
            
            //[].includes support
            engine.eval("Array.prototype.includes||Object.defineProperty(Array.prototype,\"includes\",{value:function(r,e){function t(r,e){return r===e||\"number\"==typeof r&&\"number\"==typeof e&&isNaN(r)&&isNaN(e)}if(null==this)throw new TypeError('\"this\" is null or not defined');var n=Object(this),i=n.length>>>0;if(0===i)return!1;for(var o=0|e,u=Math.max(o>=0?o:i-Math.abs(o),0);i>u;){if(t(n[u],r))return!0;u++}return!1}});");
            engine.eval("Array.prototype.forEach||(Array.prototype.forEach=function(r){var t,n;if(null==this)throw new TypeError(\"this is null or not defined\");var o=Object(this),e=o.length>>>0;if(\"function\"!=typeof r)throw new TypeError(r+\" is not a function\");for(arguments.length>1&&(t=arguments[1]),n=0;e>n;){var i;n in o&&(i=o[n],r.call(t,i,n,o)),n++}});");
            
            Compilable compilingEngine = (Compilable) engine;
            cscript = compilingEngine.compile(new BufferedReader(new InputStreamReader(EJSParser.class.getClassLoader().getResourceAsStream("org/netbeans/jcode/parser/ejs/resources/ejs.js"), "UTF-8")));
            bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
            cscript.eval(bindings);
        } catch (ScriptException | UnsupportedEncodingException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void addContext(Map<String, Object> context) {
        if (context != null) {
            context.keySet().stream().forEach((key) -> {
                try {
                    bindings.put(key, context.get(key));
                    if (context.get(key) instanceof Collection) {
                        engine.eval(String.format("%s = Java.from(%s);", key, key));
                    }
                } catch (ScriptException ex) {
                    Exceptions.printStackTrace(ex);
                }
            });
        }
    }

    public void addContext(Object context) {
        if (context != null) {
            try {
                addContext(introspect(context));
            } catch (Exception ex) {
                Logger.getLogger(EJSParser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private Map<String, Object> introspect(Object obj) {
        ObjectMapper m = new ObjectMapper();
        Map<String, Object> mappedObject = m.convertValue(obj, Map.class);
        return mappedObject;
    }

    public String parse(String template) throws ScriptException {
        String result = null;
        try {
            Object ejs = cscript.getEngine().eval("ejs");
            Invocable invocable = (Invocable) cscript.getEngine();
            Map<String, Object> options = new HashMap<>();
            options.put("filename", "template");
            if (importTemplate != null) {
                options.put("ext", importTemplate);
            }
            if (delimiter != null) {
                options.put("delimiter", delimiter);
            }
            
            result = (String) invocable.invokeMethod(ejs, "render", template, null, options);
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        }
        return result;
    }

    public String parse(Reader reader) throws ScriptException, IOException {
        StringWriter writer = new StringWriter();
        IOUtils.copy(reader, writer);
        String parsed = parse(writer.toString());
        writer.close();
        return parsed;
    }

    public void eval(String script){
        try {
            engine.eval(script);
        } catch (ScriptException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
    /**
     * @return the delimiter
     */
    public Character getDelimiter() {
        return delimiter;
    }

    /**
     * @param delimiter the delimiter to set
     */
    public void setDelimiter(Character delimiter) {
        this.delimiter = delimiter;
    }
    
    
    /**
     * @return the importTemplate
     */
    public Map<String, String> getImportTemplate() {
        return importTemplate;
    }

    /**
     * @param importTemplate the importTemplate to set
     */
    public void setImportTemplate(Map<String, String> importTemplate) {
        this.importTemplate = importTemplate;
    }
    
    
//    private static final Set<String> PARSER_FILE_TYPE = new HashSet<>(Arrays.asList(
//            "html", "js", "css", "scss", "json", "properties", "ts", "ejs", "txt", "webapp", "yml", "sh"));
    
    private static final Set<String> SKIP_FILE_TYPE = new HashSet<>(Arrays.asList(
            "png", "jpeg", "jpg", "gif"));
    

    public static boolean isTextFile(String file){
        return true;
    }
    
    public Consumer<FileTypeStream> getParserManager() {
        return getParserManager(null);
    }

    public Consumer<FileTypeStream> getParserManager(List<String> skipFile) {
        return (fileType) -> {
            try {

                if (!SKIP_FILE_TYPE.contains(fileType.getFileType()) && (skipFile == null || !SKIP_FILE_TYPE.contains(fileType.getFileName()))) {
                                        
                    Charset charset = Charset.forName("UTF-8");
                    Reader reader = new BufferedReader(new InputStreamReader(fileType.getInputStream(), charset));
                    Writer writer = new BufferedWriter(new OutputStreamWriter(fileType.getOutputStream(), charset));
                    IOUtils.write(parse(reader), writer);
                    if(!(fileType.getInputStream() instanceof ZipInputStream)){
                        reader.close();
                    }
                    writer.flush();
                    writer.close();
                } else {
                    IOUtils.copy(fileType.getInputStream(), fileType.getOutputStream());
                    if(!(fileType.getInputStream() instanceof ZipInputStream)){
                        fileType.getInputStream().close();
                    }
                    fileType.getOutputStream().close();
                }
                
            } catch (ScriptException | IOException ex) {
                Exceptions.printStackTrace(ex);
                System.out.println("Error in template : " + fileType.getFileName());
            }
        };
    }
}
