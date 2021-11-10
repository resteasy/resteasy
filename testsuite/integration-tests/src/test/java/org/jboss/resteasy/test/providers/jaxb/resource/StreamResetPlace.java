package org.jboss.resteasy.test.providers.jaxb.resource;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "place")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class StreamResetPlace {
   private String name;

   @XmlAttribute
   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }
}
