package org.jboss.resteasy.test.providers.resource.jaxbNameSpacePrefix;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for JaxbNameSpacePrefixItem complex type.
 * <p>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;complexType name="JaxbNameSpacePrefixItem">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="productName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="quantity">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}positiveInteger">
 *               &lt;maxExclusive value="100"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="USPrice" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element ref="{http://jboss.org/resteasy/test/providers/jaxb/generated/po}comment" minOccurs="0"/>
 *         &lt;element name="shipDate" type="{http://www.w3.org/2001/XMLSchema}date" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="partNum" use="required" type="{http://jboss.org/resteasy/test/providers/jaxb/generated/po}SKU" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "JaxbNameSpacePrefixItem", propOrder = {
        "productName",
        "quantity",
        "usPrice",
        "comment",
        "shipDate"
})
public class JaxbNameSpacePrefixItem {

    @XmlElement(required = true)
    protected String productName;
    protected int quantity;
    @XmlElement(name = "USPrice", required = true)
    protected BigDecimal usPrice;
    protected String comment;
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar shipDate;
    @XmlAttribute(required = true)
    protected String partNum;

    /**
     * Gets the value of the productName property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getProductName() {
        return productName;
    }

    /**
     * Sets the value of the productName property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setProductName(String value) {
        this.productName = value;
    }

    /**
     * Gets the value of the quantity property.
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Sets the value of the quantity property.
     */
    public void setQuantity(int value) {
        this.quantity = value;
    }

    /**
     * Gets the value of the usPrice property.
     *
     * @return possible object is
     * {@link BigDecimal }
     */
    public BigDecimal getUSPrice() {
        return usPrice;
    }

    /**
     * Sets the value of the usPrice property.
     *
     * @param value allowed object is
     *              {@link BigDecimal }
     */
    public void setUSPrice(BigDecimal value) {
        this.usPrice = value;
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
     * Gets the value of the shipDate property.
     *
     * @return possible object is
     * {@link XMLGregorianCalendar }
     */
    public XMLGregorianCalendar getShipDate() {
        return shipDate;
    }

    /**
     * Sets the value of the shipDate property.
     *
     * @param value allowed object is
     *              {@link XMLGregorianCalendar }
     */
    public void setShipDate(XMLGregorianCalendar value) {
        this.shipDate = value;
    }

    /**
     * Gets the value of the partNum property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getPartNum() {
        return partNum;
    }

    /**
     * Sets the value of the partNum property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPartNum(String value) {
        this.partNum = value;
    }

}
