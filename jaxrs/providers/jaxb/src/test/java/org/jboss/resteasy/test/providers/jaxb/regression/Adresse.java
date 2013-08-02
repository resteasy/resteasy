package org.jboss.resteasy.test.providers.jaxb.regression;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Adresse
{
   protected String plz;
   protected String ort;
   protected String strasse;
   protected String hausnr;

   public String getPlz()
   {
      return plz;
   }

   public void setPlz(String plz)
   {
      this.plz = plz;
   }

   public String getOrt()
   {
      return ort;
   }

   public void setOrt(String ort)
   {
      this.ort = ort;
   }

   public String getStrasse()
   {
      return strasse;
   }

   public void setStrasse(String strasse)
   {
      this.strasse = strasse;
   }

   public String getHausnr()
   {
      return hausnr;
   }

   public void setHausnr(String hausnr)
   {
      this.hausnr = hausnr;
   }
}
