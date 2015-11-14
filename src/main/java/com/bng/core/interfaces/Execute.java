/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bng.core.interfaces;

import com.bng.core.jsonBean.Event;
import com.bng.core.utils.UserCallDetail;

/**
 *
 * @author richa
 */
public interface Execute {
    
    /*
     * Implementation for execute method needs to be taken 
     * care by the child classes.
     */
    
    public String execute(Event event,String qName,UserCallDetail userCallDetail);
}
