package org.jboss.resteasy.test.providers.atom.resource;


import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@XmlRootElement(name = "created")
@XmlAccessorType(XmlAccessType.FIELD)
public class AtomComplexModelCreated {

   @XmlElement
   private Date value;

   public Date getValue() {
      return value;
   }

   public void setValue(Date created) {
      value = created;
   }

}
