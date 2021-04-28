package org.jboss.resteasy.test.providers.jaxb.resource;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "person")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class StreamResetPerson {
   private String name;

   @XmlAttribute
   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }
}
