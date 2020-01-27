package org.jboss.resteasy.test.providers.atom.resource;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

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
