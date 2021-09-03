package org.jboss.resteasy.test.providers.atom.resource;


import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "archived")
@XmlAccessorType(XmlAccessType.FIELD)
public class AtomComplexModelArchived {

   @XmlElement
   private boolean value;

   public boolean getValue() {
      return value;
   }

   public void setValue(boolean archived) {
      value = archived;
   }

}
