package org.jboss.resteasy.test.form.resource;

import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("/myform")
public interface FormResourceProxy {
   @POST
   void post(@BeanParam FormResourceClientFormSecond form2);
}
