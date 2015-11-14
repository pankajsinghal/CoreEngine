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
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
  
//Below annotation defines root element of XML file  

@XmlRootElement(name="BnGModel")
@XmlAccessorType(XmlAccessType.FIELD)
public class BnGModel implements Serializable{    
    @XmlAttribute(name="grid")
    private int grid;

    @XmlAttribute(name="guides")
    private int guides;

    @XmlAttribute(name="tooltips")
    private int tooltips;

    @XmlAttribute(name="connect")
    private int connect;

    @XmlAttribute(name="fold")
    private int fold;

    @XmlAttribute(name="page")
    private int page;

    @XmlAttribute(name="pageScale")
    private int pageScale;

    @XmlAttribute(name="pageWidth")
    private int pageWidth;

    @XmlAttribute(name="pageHeight")
    private int pageHeight;

    @XmlElementWrapper(name = "root") 
    @XmlElement(name = "mxCell" , type = MxCell.class) 
    private List<MxCell> mxCellList;

    public int getGrid() {
            return grid;
    }
    public int getGuides() {
            return guides;
    }
    public int getTooltips() {
            return tooltips;
    }
    public int getConnect() {
            return connect;
    }
    public int getFold() {
            return fold;
    }
    public int getPage() {
            return page;
    }
    public int getPageScale() {
            return pageScale;
    }
    public int getPageWidth() {
            return pageWidth;
    }
    public int getPageHeight() {
            return pageHeight;
    }
    public List<MxCell> getMxCellList() {
            return mxCellList;
    }
}