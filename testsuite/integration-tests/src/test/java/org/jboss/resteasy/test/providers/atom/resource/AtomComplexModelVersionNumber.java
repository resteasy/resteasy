package org.jboss.resteasy.test.providers.atom.resource;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

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
