package org.jboss.resteasy.test.providers.jaxb.resource;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for itemtype complex type.
 * <p>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;complexType name="itemtype">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="title" type="{http://jboss.org/resteasy/test/providers/jaxb/generated/order}stringtype"/>
 *         &lt;element name="note" type="{http://jboss.org/resteasy/test/providers/jaxb/generated/order}stringtype" minOccurs="0"/>
 *         &lt;element name="quantity" type="{http://jboss.org/resteasy/test/providers/jaxb/generated/order}inttype"/>
 *         &lt;element name="price" type="{http://jboss.org/resteasy/test/providers/jaxb/generated/order}dectype"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "itemtype", propOrder = {
        "title",
        "note",
        "quantity",
        "price"
})
public class Itemtype {

    @XmlElement(required = true)
    protected String title;
    protected String note;
    @XmlElement(required = true)
    protected BigInteger quantity;
    @XmlElement(required = true)
    protected BigDecimal price;

    /**
     * Gets the value of the title property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the value of the title property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * Gets the value of the note property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getNote() {
        return note;
    }

    /**
     * Sets the value of the note property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setNote(String value) {
        this.note = value;
    }

    /**
     * Gets the value of the quantity property.
     *
     * @return possible object is
     * {@link BigInteger }
     */
    public BigInteger getQuantity() {
        return quantity;
    }

    /**
     * Sets the value of the quantity property.
     *
     * @param value allowed object is
     *              {@link BigInteger }
     */
    public void setQuantity(BigInteger value) {
        this.quantity = value;
    }

    /**
     * Gets the value of the price property.
     *
     * @return possible object is
     * {@link BigDecimal }
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * Sets the value of the price property.
     *
     * @param value allowed object is
     *              {@link BigDecimal }
     */
    public void setPrice(BigDecimal value) {
        this.price = value;
    }

}
