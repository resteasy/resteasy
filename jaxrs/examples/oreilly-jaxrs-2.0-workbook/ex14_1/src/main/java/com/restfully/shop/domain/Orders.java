package com.restfully.shop.domain;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@XmlRootElement(name = "orders")
public class Orders
{
   protected Collection<Order> orders = new ArrayList<Order>();
   protected List<AtomLink> links;

   @XmlElementRef
   public Collection<Order> getOrders()
   {
      return orders;
   }

   public void setOrders(Collection<Order> orders)
   {
      this.orders = orders;
   }

   @XmlElementRef
   public List<AtomLink> getLinks()
   {
      return links;
   }

   public void setLinks(List<AtomLink> links)
   {
      this.links = links;
   }

   @XmlTransient
   public String getNext()
   {
      if (links == null) return null;
      for (AtomLink link : links)
      {
         if ("next".equals(link.getRelationship())) return link.getHref();
      }
      return null;
   }

   @XmlTransient
   public String getPrevious()
   {
      if (links == null) return null;
      for (AtomLink link : links)
      {
         if ("previous".equals(link.getRelationship())) return link.getHref();
      }
      return null;
   }

}