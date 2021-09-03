package org.jboss.resteasy.test.providers.jaxb.resource;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class AbstractJaxbClassCompanyCustomer extends AbstractJaxbClassCustomer {
   private static final long serialVersionUID = 3224665468219250145L;

   private short rabatt;

   public AbstractJaxbClassCompanyCustomer() {
      super();
   }

   public short getRabatt() {
      return rabatt;
   }

   public void setRabatt(short rabatt) {
      this.rabatt = rabatt;
   }

   @Override
   public String getArt() {
      return "COMPANYCUSTOMER";
   }

   @Override
   public String toString() {
      return "{" + super.toString() + ", rabatt=" + rabatt + '}';
   }
}
