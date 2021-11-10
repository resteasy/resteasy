package org.jboss.resteasy.test.providers.jaxb.resource;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ordertype", propOrder =
      {"person", "shipto", "items"})
public class Order {

   private String person;

   private ShipTo shipto;

   @XmlElement(name = "item", required = true)
   private List<Item> items = new ArrayList<Item>();

   @XmlAttribute(required = true)
   private String orderid;

   /**
    * Get the person.
    *
    * @return the person.
    */
   public String getPerson() {
      return person;
   }

   /**
    * Set the person.
    *
    * @param person The person to set.
    */
   public void setPerson(String person) {
      this.person = person;
   }

   /**
    * Get the shipto.
    *
    * @return the shipto.
    */
   public ShipTo getShipto() {
      return shipto;
   }

   /**
    * Set the shipto.
    *
    * @param shipto The shipto to set.
    */
   public void setShipto(ShipTo shipto) {
      this.shipto = shipto;
   }

   /**
    * Add item to items
    *
    * @param item
    */
   public void addItem(Item item) {
      item.setOrder(this);
      items.add(item);
   }

   /**
    * Get the item.
    *
    * @return the item.
    */
   public List<Item> getItems() {
      return items;
   }

   public Item getItem(int index) {
      return items.get(index);
   }

   /**
    * Remove item from items
    *
    * @param item
    */
   public void removeItem(Item item) {
      items.remove(item);
   }

   /**
    * Remove item on specified index
    *
    * @param index
    * @return
    */
   public Item removeItem(int index) {
      return items.remove(index);
   }

   /**
    * Set the item.
    *
    * @param item The item to set.
    */
   public void setItems(List<Item> items) {
      this.items = items;
   }

   /**
    * Get the orderid.
    *
    * @return the orderid.
    */
   public String getOrderid() {
      return orderid;
   }

   /**
    * Set the orderid.
    *
    * @param orderid The orderid to set.
    */
   public void setOrderid(String orderid) {
      this.orderid = orderid;
   }
}
