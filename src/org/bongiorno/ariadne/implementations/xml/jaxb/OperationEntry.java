//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.3 in JDK 1.6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.01.16 at 11:26:07 AM PST 
//


package org.bongiorno.ariadne.implementations.xml.jaxb;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.bongiorno.ariadne.interfaces.Operation;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}ID"/>
 *         &lt;element ref="{}LHO"/>
 *         &lt;element ref="{}RHO"/>
 *         &lt;element ref="{}OP"/>
 *         &lt;element ref="{}Description"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "id",
    "lho",
    "rho",
    "op",
    "description"
})
@XmlRootElement(name = "OperationEntry")
@Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2009-01-16T11:26:07-08:00", comments = "JAXB RI vJAXB 2.1.3 in JDK 1.6")
public class OperationEntry {

    @XmlElement(name = "ID")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2009-01-16T11:26:07-08:00", comments = "JAXB RI vJAXB 2.1.3 in JDK 1.6")
    protected int id;
    @XmlElement(name = "LHO")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2009-01-16T11:26:07-08:00", comments = "JAXB RI vJAXB 2.1.3 in JDK 1.6")
    protected int lho;
    @XmlElement(name = "RHO")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2009-01-16T11:26:07-08:00", comments = "JAXB RI vJAXB 2.1.3 in JDK 1.6")
    protected int rho;
    @XmlElement(name = "OP")
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2009-01-16T11:26:07-08:00", comments = "JAXB RI vJAXB 2.1.3 in JDK 1.6")
    protected int op;
    @XmlElement(name = "Description", required = true)
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2009-01-16T11:26:07-08:00", comments = "JAXB RI vJAXB 2.1.3 in JDK 1.6")
    protected String description;

    public OperationEntry() {
    }

    public OperationEntry(int id, int lho, int op, int rho,  String description) {
        this.id = id;
        this.lho = lho;
        this.rho = rho;
        this.op = op;
        this.description = description;
    }

    public OperationEntry(int i, OperandOwnerEntry lho, OperatorEntry ope, OperandOwnerEntry rho, String description) {
        this(i,lho.getID(),ope.getID(),rho.getID(),description);
    }

    public OperationEntry(Operation o) {
        this(o.getId(),o.getLho().getId(), o.getOperator().getId(), o.getRho().getId(),o.toString());
    }

    /**
     * Gets the value of the id property.
     * 
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2009-01-16T11:26:07-08:00", comments = "JAXB RI vJAXB 2.1.3 in JDK 1.6")
    public int getID() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2009-01-16T11:26:07-08:00", comments = "JAXB RI vJAXB 2.1.3 in JDK 1.6")
    public void setID(int value) {
        this.id = value;
    }

    /**
     * Gets the value of the lho property.
     * 
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2009-01-16T11:26:07-08:00", comments = "JAXB RI vJAXB 2.1.3 in JDK 1.6")
    public int getLHO() {
        return lho;
    }

    /**
     * Sets the value of the lho property.
     * 
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2009-01-16T11:26:07-08:00", comments = "JAXB RI vJAXB 2.1.3 in JDK 1.6")
    public void setLHO(int value) {
        this.lho = value;
    }

    /**
     * Gets the value of the rho property.
     * 
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2009-01-16T11:26:07-08:00", comments = "JAXB RI vJAXB 2.1.3 in JDK 1.6")
    public int getRHO() {
        return rho;
    }

    /**
     * Sets the value of the rho property.
     * 
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2009-01-16T11:26:07-08:00", comments = "JAXB RI vJAXB 2.1.3 in JDK 1.6")
    public void setRHO(int value) {
        this.rho = value;
    }

    /**
     * Gets the value of the op property.
     * 
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2009-01-16T11:26:07-08:00", comments = "JAXB RI vJAXB 2.1.3 in JDK 1.6")
    public int getOP() {
        return op;
    }

    /**
     * Sets the value of the op property.
     * 
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2009-01-16T11:26:07-08:00", comments = "JAXB RI vJAXB 2.1.3 in JDK 1.6")
    public void setOP(int value) {
        this.op = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2009-01-16T11:26:07-08:00", comments = "JAXB RI vJAXB 2.1.3 in JDK 1.6")
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    @Generated(value = "com.sun.tools.internal.xjc.Driver", date = "2009-01-16T11:26:07-08:00", comments = "JAXB RI vJAXB 2.1.3 in JDK 1.6")
    public void setDescription(String value) {
        this.description = value;
    }

}
