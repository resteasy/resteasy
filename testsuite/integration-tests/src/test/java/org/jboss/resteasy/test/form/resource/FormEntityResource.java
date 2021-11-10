package org.jboss.resteasy.test.form.resource;

import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("test")
public class FormEntityResource {

   @POST
   @Path("form")
   public String formParam(@FormParam("fp") String fp, String content) {
      return fp + "|" + content;
   }
}
