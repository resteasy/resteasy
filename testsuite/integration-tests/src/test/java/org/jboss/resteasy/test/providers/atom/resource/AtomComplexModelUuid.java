package org.jboss.resteasy.test.providers.atom.resource;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "uuid")
@XmlAccessorType(XmlAccessType.FIELD)
public class AtomComplexModelUuid {

   @XmlElement
   private String value;

   public String getValue() {
      return value;
   }

   public void setValue(String uuid) {
      value = uuid;
   }

}
