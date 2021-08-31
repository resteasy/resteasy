package org.jboss.resteasy.test.client.proxy.resource;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ProxyJaxbCredit {
   private String name;

   @XmlElement
   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }
}
