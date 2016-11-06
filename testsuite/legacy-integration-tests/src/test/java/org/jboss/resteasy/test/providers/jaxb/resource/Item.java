package org.jboss.resteasy.test.providers.jaxb.resource;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "itemtype", propOrder = {
        "title",
        "note",
        "quantity",
        "price"
})
public class Item {
    @XmlElement(required = true)
    private String title;

    private String note;

    @XmlElement(required = true)
    private Integer quantity;

    @XmlElement(required = true)
    private Double price;

    @XmlTransient
    private Order order;

    /**
     * Get the order.
     *
     * @return the order.
     */
    public Order getOrder() {
        return order;
    }

    /**
     * Set the order.
     *
     * @param order The order to set.
     */
    public void setOrder(Order order) {
        this.order = order;
    }

    /**
     * Get the title.
     *
     * @return the title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the title.
     *
     * @param title The title to set.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get the note.
     *
     * @return the note.
     */
    public String getNote() {
        return note;
    }

    /**
     * Set the note.
     *
     * @param note The note to set.
     */
    public void setNote(String note) {
        this.note = note;
    }

    /**
     * Get the quantity.
     *
     * @return the quantity.
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * Set the quantity.
     *
     * @param quantity The quantity to set.
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * Get the price.
     *
     * @return the price.
     */
    public Double getPrice() {
        return price;
    }

    /**
     * Set the price.
     *
     * @param price The price to set.
     */
    public void setPrice(Double price) {
        this.price = price;
    }

    /**
     * JAXB Callback method used to reassociate the item with the owning Order.
     *
     * @param unmarshaller the JAXB {@link Unmarshaller}.
     * @param parent       the owning {@link Contact} instance.
     */
    public void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
        this.setOrder((Order) order);
    }
}
