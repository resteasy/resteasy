package org.jboss.resteasy.test.providers.jaxb.regression;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public final class Privatkunde extends Kunde
{
   private static final long serialVersionUID = 133152931415808605L;

   @Override
   public String getArt()
   {
      return "PRIVATKUNDE";
   }

   @Override
   public String toString()
   {
      return "{" + super.toString() + ", familienstand=nada'}'";
   }
}
