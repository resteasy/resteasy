package org.jboss.resteasy.test.providers.jaxb.regression.resteasy175;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Collection;

@XmlRootElement(name = "kunden")
public class KundeList
{
   @XmlElementRef
   public Collection<Kunde> kunden;

   public KundeList()
   {
   }

   public KundeList(Collection<Kunde> kunden)
   {
      this.kunden = kunden;
   }
}
