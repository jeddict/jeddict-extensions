/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.jcode.layer;

import static org.netbeans.jcode.layer.Technology.Type.BUSINESS;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author jGauravGupta <gaurav.gupta.jc@gmail.com>
 */
@ServiceProvider(service=Generator.class)
@Technology(type=BUSINESS)
public class DefaultBusinessLayer implements Generator {
    
}
