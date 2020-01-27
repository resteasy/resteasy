package org.jboss.resteasy.test.providers.atom.resource;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "format")
@XmlAccessorType(XmlAccessType.FIELD)
public class AtomComplexModelFormat {

   @XmlElement
   private String value;

   public String getValue() {
      return value;
   }

   public void setValue(String format) {
      value = format;
   }

}
