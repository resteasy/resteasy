package org.jboss.resteasy.test.providers.jaxb.resource;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "human")
@XmlAccessorType(XmlAccessType.FIELD)
public class XmlJavaTypeAdapterHuman {
   @XmlElement
   private String name;

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }
}
