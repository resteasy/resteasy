package org.jboss.resteasy.test.providers.jaxb.link;

import jakarta.ws.rs.core.Link;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@XmlRootElement(name = "customer")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Customer
{
   private String name;
   private List<Link> links = new ArrayList<Link>();

   public Customer()
   {
   }

   public Customer(final String name)
   {
      this.name = name;
   }

   @XmlElement
   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   @XmlElement(name = "link")
   public List<Link> getLinks()
   {
      return links;
   }
}
