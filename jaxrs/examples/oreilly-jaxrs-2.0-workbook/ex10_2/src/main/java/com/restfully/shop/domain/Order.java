package com.restfully.shop.domain;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@XmlRootElement(name = "order")
@XmlType(propOrder = {"total", "date", "cancelled", "customer", "lineItems"})
public class Order
{
   protected int id;
   protected boolean cancelled;
   protected List<LineItem> lineItems;
   protected String total;
   protected String date;
   protected Customer customer;

   @XmlAttribute
   public int getId()
   {
      return id;
   }

   public void setId(int id)
   {
      this.id = id;
   }

   public boolean isCancelled()
   {
      return cancelled;
   }

   public void setCancelled(boolean cancelled)
   {
      this.cancelled = cancelled;
   }

   @XmlElementWrapper(name = "line-items")
   public List<LineItem> getLineItems()
   {
      return lineItems;
   }

   public void setLineItems(List<LineItem> lineItems)
   {
      this.lineItems = lineItems;
   }

   public String getDate()
   {
      return date;
   }

   public void setDate(String date)
   {
      this.date = date;
   }

   public String getTotal()
   {
      return total;
   }

   public void setTotal(String total)
   {
      this.total = total;
   }

   @XmlElementRef
   public Customer getCustomer()
   {
      return customer;
   }

   public void setCustomer(Customer customer)
   {
      this.customer = customer;
   }
}
