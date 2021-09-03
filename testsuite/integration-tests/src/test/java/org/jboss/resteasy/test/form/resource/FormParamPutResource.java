package org.jboss.resteasy.test.form.resource;

import org.jboss.logging.Logger;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;

@Path("/test")
public class FormParamPutResource {

   private static Logger logger = Logger.getLogger(FormParamPutResource.class);
   private static volatile String formParam;

   @PUT
   @Path("/{pathParam:\\d+}")
   @Consumes("application/x-www-form-urlencoded")
   @Produces({"application/xml", "application/json"})
   public void updateGuestPostStatus(@PathParam("pathParam") Long pathParam,
                                      @FormParam("formParam") String formParam) {

      this.formParam = formParam;
      logger.info("===============");
      logger.info(formParam);
      logger.info("===============");
   }

   @GET
   public String getStatus() {
      return formParam;
   }

}
