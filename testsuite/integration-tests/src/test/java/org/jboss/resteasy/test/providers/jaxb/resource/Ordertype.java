package org.jboss.resteasy.test.providers.jaxb.resource;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ordertype complex type.
 * <p>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;complexType name="ordertype">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="person" type="{http://jboss.org/resteasy/test/providers/jaxb/generated/order}stringtype"/>
 *         &lt;element name="shipto" type="{http://jboss.org/resteasy/test/providers/jaxb/generated/order}shiptotype"/>
 *         &lt;element name="item" type="{http://jboss.org/resteasy/test/providers/jaxb/generated/order}itemtype" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="orderid" use="required" type="{http://jboss.org/resteasy/test/providers/jaxb/generated/order}orderidtype" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ordertype", propOrder = {
        "person",
        "shipto",
        "item"
})
public class Ordertype {

    @XmlElement(required = true)
    protected String person;
    @XmlElement(required = true)
    protected Shiptotype shipto;
    @XmlElement(required = true)
    protected List<Itemtype> item;
    @XmlAttribute(required = true)
    protected String orderid;

    /**
     * Gets the value of the person property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getPerson() {
        return person;
    }

    /**
     * Sets the value of the person property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setPerson(String value) {
        this.person = value;
    }

    /**
     * Gets the value of the shipto property.
     *
     * @return possible object is
     * {@link Shiptotype }
     */
    public Shiptotype getShipto() {
        return shipto;
    }

    /**
     * Sets the value of the shipto property.
     *
     * @param value allowed object is
     *              {@link Shiptotype }
     */
    public void setShipto(Shiptotype value) {
        this.shipto = value;
    }

    /**
     * Gets the value of the item property.
     * <p>
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the item property.
     * <p>
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getItem().add(newItem);
     * </pre>
     * <p>
     * <p>
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Itemtype }
     */
    public List<Itemtype> getItem() {
        if (item == null) {
            item = new ArrayList<Itemtype>();
        }
        return this.item;
    }

    /**
     * Gets the value of the orderid property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getOrderid() {
        return orderid;
    }

    /**
     * Sets the value of the orderid property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setOrderid(String value) {
        this.orderid = value;
    }

}
