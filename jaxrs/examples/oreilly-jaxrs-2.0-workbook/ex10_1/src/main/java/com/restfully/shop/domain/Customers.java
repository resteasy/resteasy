package com.restfully.shop.domain;

import javax.ws.rs.core.Link;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Collection;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@XmlRootElement(name = "customers")
public class Customers
{
   protected Collection<Customer> customers;
   protected List<AtomLink> links;

   @XmlElementRef
   public Collection<Customer> getCustomers()
   {
      return customers;
   }

   public void setCustomers(Collection<Customer> customers)
   {
      this.customers = customers;
   }

   @XmlElement
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
