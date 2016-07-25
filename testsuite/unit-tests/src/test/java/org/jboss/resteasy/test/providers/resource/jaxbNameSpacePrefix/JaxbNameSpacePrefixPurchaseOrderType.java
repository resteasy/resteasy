package org.jboss.resteasy.test.providers.resource.jaxbNameSpacePrefix;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for JaxbNameSpacePrefixPurchaseOrderType complex type.
 * <p>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;complexType name="JaxbNameSpacePrefixPurchaseOrderType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="shipTo" type="{http://jboss.org/resteasy/test/providers/jaxb/generated/po}JaxbNameSpacePrefixUSAddress"/>
 *         &lt;element name="billTo" type="{http://jboss.org/resteasy/test/providers/jaxb/generated/po}JaxbNameSpacePrefixUSAddress"/>
 *         &lt;element ref="{http://jboss.org/resteasy/test/providers/jaxb/generated/po}comment" minOccurs="0"/>
 *         &lt;element name="jaxbNameSpacePrefixItems" type="{http://jboss.org/resteasy/test/providers/jaxb/generated/po}JaxbNameSpacePrefixItems"/>
 *       &lt;/sequence>
 *       &lt;attribute name="orderDate" type="{http://www.w3.org/2001/XMLSchema}date" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "JaxbNameSpacePrefixPurchaseOrderType", propOrder = {
        "shipTo",
        "billTo",
        "comment",
        "jaxbNameSpacePrefixItems"
})
public class JaxbNameSpacePrefixPurchaseOrderType {

    @XmlElement(required = true)
    protected JaxbNameSpacePrefixUSAddress shipTo;
    @XmlElement(required = true)
    protected JaxbNameSpacePrefixUSAddress billTo;
    protected String comment;
    @XmlElement(required = true)
    protected JaxbNameSpacePrefixItems jaxbNameSpacePrefixItems;
    @XmlAttribute
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar orderDate;

    /**
     * Gets the value of the shipTo property.
     *
     * @return possible object is
     * {@link JaxbNameSpacePrefixUSAddress }
     */
    public JaxbNameSpacePrefixUSAddress getShipTo() {
        return shipTo;
    }

    /**
     * Sets the value of the shipTo property.
     *
     * @param value allowed object is
     *              {@link JaxbNameSpacePrefixUSAddress }
     */
    public void setShipTo(JaxbNameSpacePrefixUSAddress value) {
        this.shipTo = value;
    }

    /**
     * Gets the value of the billTo property.
     *
     * @return possible object is
     * {@link JaxbNameSpacePrefixUSAddress }
     */
    public JaxbNameSpacePrefixUSAddress getBillTo() {
        return billTo;
    }

    /**
     * Sets the value of the billTo property.
     *
     * @param value allowed object is
     *              {@link JaxbNameSpacePrefixUSAddress }
     */
    public void setBillTo(JaxbNameSpacePrefixUSAddress value) {
        this.billTo = value;
    }

    /**
     * Gets the value of the comment property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getComment() {
        return comment;
    }

    /**
     * Sets the value of the comment property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setComment(String value) {
        this.comment = value;
    }

    /**
     * Gets the value of the jaxbNameSpacePrefixItems property.
     *
     * @return possible object is
     * {@link JaxbNameSpacePrefixItems }
     */
    public JaxbNameSpacePrefixItems getJaxbNameSpacePrefixItems() {
        return jaxbNameSpacePrefixItems;
    }

    /**
     * Sets the value of the jaxbNameSpacePrefixItems property.
     *
     * @param value allowed object is
     *              {@link JaxbNameSpacePrefixItems }
     */
    public void setJaxbNameSpacePrefixItems(JaxbNameSpacePrefixItems value) {
        this.jaxbNameSpacePrefixItems = value;
    }

    /**
     * Gets the value of the orderDate property.
     *
     * @return possible object is
     * {@link XMLGregorianCalendar }
     */
    public XMLGregorianCalendar getOrderDate() {
        return orderDate;
    }

    /**
     * Sets the value of the orderDate property.
     *
     * @param value allowed object is
     *              {@link XMLGregorianCalendar }
     */
    public void setOrderDate(XMLGregorianCalendar value) {
        this.orderDate = value;
    }

}
