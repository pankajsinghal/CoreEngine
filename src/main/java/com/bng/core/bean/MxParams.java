/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bng.core.bean;

import java.io.Serializable;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
/**
 *
 * @author richa
 */
@XmlRootElement(namespace = "MxCell")  
@XmlAccessorType(XmlAccessType.FIELD)
public class MxParams implements Serializable{
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@XmlElementWrapper(name = "mxParams") 
    @XmlElement(name = "mxParam" , type = MxParam.class)  
    private ArrayList<MxParam> listOfMxParam;

    public ArrayList<MxParam> getListOfMxParam() {
            return listOfMxParam;
    }
}