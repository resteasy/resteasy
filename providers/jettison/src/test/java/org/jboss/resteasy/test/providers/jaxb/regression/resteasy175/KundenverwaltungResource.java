package org.jboss.resteasy.test.providers.jaxb.regression.resteasy175;

import org.jboss.resteasy.annotations.providers.jaxb.json.Mapped;
import org.jboss.resteasy.annotations.providers.jaxb.json.XmlNsMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;


@Path("/kundenverwaltung")
@Produces(APPLICATION_XML)
@Consumes(APPLICATION_XML)
public class KundenverwaltungResource
{
   /**
    * Beispiel mit JSON
    * Funktioniert nicht mit RESTEASY: https://jira.jboss.org/jira/browse/RESTEASY-175
    */
   @GET
   @Path("/kunden")
   @Produces(APPLICATION_JSON)
   @Mapped(namespaceMap = {
           @XmlNsMap(namespace = "http://hska.de/kundenverwaltung", jsonName = "kunden")
   })
   public KundeList findKundenJSON()
   {

      final List<Kunde> kunden = new ArrayList<Kunde>(4);
      for (int i = 0; i < 4; i++)
      {
         final Kunde k = new Kunde();
         k.setId(Long.valueOf(i));
         k.setNachname("Lastname" + i);
         k.setSeit(new Date());

         kunden.add(k);
      }

      // Konvertierung in eigene List-Klasse wg. Wurzelelement
      final KundeList kundeList = new KundeList(kunden);

      return kundeList;
   }
}
