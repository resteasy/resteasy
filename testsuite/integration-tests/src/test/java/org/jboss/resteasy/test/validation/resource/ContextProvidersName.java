package org.jboss.resteasy.test.validation.resource;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "name")
@XmlAccessorType(XmlAccessType.FIELD)
public class ContextProvidersName {
   @XmlElement
   private String name;

   public ContextProvidersName() {
   }

   public ContextProvidersName(final String name) {
      this.name = name;
   }

   public String getName() {
      return name;
   }

   public boolean equals(Object o) {
      if (o == null) {
         return false;
      }
      if (!(o instanceof ContextProvidersName)) {
         return false;
      }
      return name.equals(((ContextProvidersName) o).getName());
   }

   public int hashCode() {
      return super.hashCode();
   }
}
