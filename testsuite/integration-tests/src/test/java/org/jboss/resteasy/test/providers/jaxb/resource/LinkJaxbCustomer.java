package org.jboss.resteasy.test.providers.jaxb.resource;

import jakarta.ws.rs.core.Link;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "customer")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class LinkJaxbCustomer {
   private String name;
   private List<Link> links = new ArrayList<Link>();

   public LinkJaxbCustomer() {
   }

   public LinkJaxbCustomer( final String name) {
      this.name = name;
   }

   @XmlElement
   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   @XmlElement(name = "link")
   public List<Link> getLinks() {
      return links;
   }
}
