/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.jcode.layer;

import java.io.IOException;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.SourceGroup;
import org.netbeans.jcode.entity.info.EntityResourceBeanModel;
import static org.netbeans.jcode.layer.Technology.Type.CONTROLLER;
import org.netbeans.jcode.task.progress.ProgressHandler;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author jGauravGupta <gaurav.gupta.jc@gmail.com>
 */
@ServiceProvider(service=Generator.class)
@Technology(type=CONTROLLER)
public class DefaultControllerLayer implements Generator {

    @Override
    public void execute(Project project, SourceGroup source, EntityResourceBeanModel model, ProgressHandler handler) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
