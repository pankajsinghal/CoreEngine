/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bng.core.bean;

/**
 *
 * @author richa
 */
import java.io.Serializable;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
  
//Below statement means that class "IVR.java" is the root-element of our example  
@XmlRootElement(namespace = "root")  
@XmlAccessorType(XmlAccessType.FIELD)
public class MxCell implements Serializable
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@XmlAttribute(name="id")
    private int id;
    
    @XmlAttribute(name="type")
    private String type;
    
    @XmlAttribute(name="value")
    private String value;
    
    @XmlAttribute(name="source")
    private int source;
    
    @XmlAttribute(name="target")
    private int target;   
        
    @XmlElementWrapper(name = "mxParams") 
    @XmlElement(name = "mxParam" , type = MxParam.class)  
    private ArrayList<MxParam> listOfMxParam;
    	
    public int getId() {
            return id;
    }
    public String getType(){
        return type;
    }
    public String getValue() {
            return value;
    }
    public int getSource() {
            return source;
    }
    public int getTarget() {
            return target;
    }
    public ArrayList<MxParam> getListOfMxParam() {
            return listOfMxParam;
    }    
}