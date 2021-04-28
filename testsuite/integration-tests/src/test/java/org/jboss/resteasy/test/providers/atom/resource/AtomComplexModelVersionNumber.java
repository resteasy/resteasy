package org.jboss.resteasy.test.providers.atom.resource;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "versionNumber")
@XmlAccessorType(XmlAccessType.FIELD)
public class AtomComplexModelVersionNumber {

   @XmlElement
   private long value;

   public long getValue() {
      return value;
   }

   public void setValue(long uuid) {
      value = uuid;
   }

}
