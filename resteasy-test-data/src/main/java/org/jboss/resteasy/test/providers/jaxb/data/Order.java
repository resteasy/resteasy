package org.jboss.resteasy.test.providers.jaxb.data;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * A Order.
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ordertype", propOrder =
{"person", "shipto", "items"})
public class Order
{

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
   public String getPerson()
   {
      return person;
   }

   /**
    * Set the person.
    * 
    * @param person
    *           The person to set.
    */
   public void setPerson(String person)
   {
      this.person = person;
   }

   /**
    * Get the shipto.
    * 
    * @return the shipto.
    */
   public ShipTo getShipto()
   {
      return shipto;
   }

   /**
    * Set the shipto.
    * 
    * @param shipto
    *           The shipto to set.
    */
   public void setShipto(ShipTo shipto)
   {
      this.shipto = shipto;
   }

   /**
    * FIXME Comment this
    * 
    * @param item
    */
   public void addItem(Item item)
   {
      item.setOrder(this);
      items.add(item);
   }

   /**
    * Get the item.
    * 
    * @return the item.
    */
   public List<Item> getItems()
   {
      return items;
   }

   public Item getItem(int index)
   {
      return items.get(index);
   }

   /**
    * FIXME Comment this
    * 
    * @param item
    */
   public void removeItem(Item item)
   {
      items.remove(item);
   }

   /**
    * FIXME Comment this
    * 
    * @param index
    * @return
    */
   public Item removeItem(int index)
   {
      return items.remove(index);
   }

   /**
    * Set the item.
    * 
    * @param item
    *           The item to set.
    */
   public void setItems(List<Item> items)
   {
      this.items = items;
   }

   /**
    * Get the orderid.
    * 
    * @return the orderid.
    */
   public String getOrderid()
   {
      return orderid;
   }

   /**
    * Set the orderid.
    * 
    * @param orderid
    *           The orderid to set.
    */
   public void setOrderid(String orderid)
   {
      this.orderid = orderid;
   }
}
