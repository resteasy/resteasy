package org.jboss.resteasy.test.providers.jaxb.resource;

import org.jboss.logging.Logger;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/test")
public class QualityFactorResource {

   private static Logger logger = Logger.getLogger(QualityFactorResource.class.getName());

   @GET
   @Produces({"application/json", "application/xml"})
   public QualityFactorThing get(@HeaderParam("Accept") String accept) {
      logger.info(accept);
      QualityFactorThing thing = new QualityFactorThing();
      thing.setName("Bill");
      return thing;
   }
}
