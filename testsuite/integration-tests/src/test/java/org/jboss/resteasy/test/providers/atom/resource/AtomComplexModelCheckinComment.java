package org.jboss.resteasy.test.providers.atom.resource;


import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "checkinComment")
@XmlAccessorType(XmlAccessType.FIELD)
public class AtomComplexModelCheckinComment {

   @XmlElement
   private String value;

   public String getValue() {
      return value;  //To change body of created methods use File | Settings | File Templates.
   }

   public void setValue(String checkin) {
      value = checkin;
   }

}
