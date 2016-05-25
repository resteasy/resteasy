package org.jboss.resteasy.test.providers.jaxb.regression;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Firmenkunde extends Kunde
{
   private static final long serialVersionUID = 3224665468219250145L;

   private short rabatt;

   public Firmenkunde()
   {
      super();
   }

   public short getRabatt()
   {
      return rabatt;
   }

   public void setRabatt(short rabatt)
   {
      this.rabatt = rabatt;
   }

   @Override
   public String getArt()
   {
      return "FIRMENKUNDE";
   }

   @Override
   public String toString()
   {
      return "{" + super.toString() + ", rabatt=" + rabatt + '}';
   }
}