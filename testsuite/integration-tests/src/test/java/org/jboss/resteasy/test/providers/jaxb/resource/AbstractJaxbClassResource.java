package org.jboss.resteasy.test.providers.jaxb.resource;

import org.jboss.logging.Logger;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("/")
public class AbstractJaxbClassResource {

   private static Logger logger = Logger.getLogger(AbstractJaxbClassResource.class.getName());

   @POST
   public void post(AbstractJaxbClassPerson person) {
      logger.info(person.getName() + " " + person.getId());
   }

   @POST
   @Path("customer")
   public void postKunde(AbstractJaxbClassCustomer customer) {
      logger.info(customer.getNachname());
   }

}
