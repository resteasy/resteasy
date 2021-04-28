package org.jboss.resteasy.test.providers.jaxb.resource;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public final class AbstractJaxbClassPrivatCustomer extends AbstractJaxbClassCustomer {
   private static final long serialVersionUID = 133152931415808605L;

   @Override
   public String getArt() {
      return "PRIVATCUSTOMER";
   }

   @Override
   public String toString() {
      return "{" + super.toString() + ", familienstand=nada'}'";
   }
}
