package org.jboss.resteasy.test.providers.atom.resource;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "disabled")
@XmlAccessorType(XmlAccessType.FIELD)
public class AtomComplexModelDisabled {

   @XmlElement
   private boolean value;

   public boolean getValue() {
      return value;
   }

   public void setValue(boolean disabled) {
      value = disabled;
   }

}
