package org.jboss.resteasy.test.providers.atom.resource;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

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
