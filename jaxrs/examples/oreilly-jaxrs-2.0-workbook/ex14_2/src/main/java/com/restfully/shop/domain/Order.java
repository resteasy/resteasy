package com.restfully.shop.domain;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@XmlRootElement(name = "order")
@XmlType(propOrder = {"total", "date", "cancelled", "customer", "lineItems", "links"})
public class Order
{
   protected int id;
   protected boolean cancelled;
   protected List<LineItem> lineItems = new ArrayList<LineItem>();
   protected double total;
   protected String date;
   protected Customer customer;
   protected List<Link> links;

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

   public double getTotal()
   {
      return total;
   }

   public void setTotal(double total)
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

   @XmlElement(name="link")
   @XmlJavaTypeAdapter(Link.JaxbAdapter.class)
   public List<Link> getLinks()
   {
      return links;
   }

   public void setLinks(List<Link> links)
   {
      this.links = links;
   }

   public void addLink(Link link)
   {
      if (links == null) links = new ArrayList<Link>();
      links.add(link);
   }

   @Override
   public String toString()
   {
      return "Order{" +
              "id=" + id +
              ", cancelled=" + cancelled +
              ", lineItems=" + lineItems +
              ", total=" + total +
              ", date='" + date + '\'' +
              ", customer=" + customer +
              '}';
   }
}
