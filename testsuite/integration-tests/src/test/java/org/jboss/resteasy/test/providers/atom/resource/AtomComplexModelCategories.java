package org.jboss.resteasy.test.providers.atom.resource;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "categories")
@XmlAccessorType(XmlAccessType.FIELD)
public class AtomComplexModelCategories {

   @XmlElement(name = "value")
   private String[] values;

   public String[] getValues() {
      return values;
   }

   public void setValue(String[] categories) {
      values = categories;
   }

}
