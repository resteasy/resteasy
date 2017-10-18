package org.jboss.resteasy.test.form.resource;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("test")
public class FormEntityResource {

   @POST
   @Path("form")
   public String formParam(@FormParam("fp") String fp, String content) {
      return fp + "|" + content;
   }
}
