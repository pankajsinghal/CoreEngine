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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
  
//Below statement means that class "IVR.java" is the root-element of our example  
@XmlRootElement(namespace = "MxCell") 
@XmlAccessorType(XmlAccessType.FIELD)
public class MxGeometry implements Serializable
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@XmlAttribute(name="x")
    private int x;
    
    @XmlAttribute(name="y")
    private int y;
    
    @XmlAttribute(name="width")
    private int width;
	
    @XmlAttribute(name="height")
    private int height;
	
    @XmlAttribute(name="relative")
    private int relative;
	
    @XmlAttribute(name="as")
    private String as;
    
    public int getX() {
            return x;
    }
    public int getY() {
            return y;
    }
    public int getWidth() {
            return width;
    }
    public int getHeight() {
            return height;
    }
    public int getRelative() {
            return relative;
    }
    public String getAs() {
            return as;
    }   
}