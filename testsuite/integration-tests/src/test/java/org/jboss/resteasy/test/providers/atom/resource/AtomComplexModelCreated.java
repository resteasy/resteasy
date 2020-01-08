package org.jboss.resteasy.test.providers.atom.resource;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
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
