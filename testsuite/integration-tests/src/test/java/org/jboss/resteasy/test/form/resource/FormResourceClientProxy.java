package org.jboss.resteasy.test.form.resource;

import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;

@Path("/form/{id}")
public interface FormResourceClientProxy {
   @Produces(MediaType.APPLICATION_FORM_URLENCODED)
   @POST
   MultivaluedMap<String, String> post(@BeanParam FormResourceClientForm form);

   @Produces(MediaType.APPLICATION_FORM_URLENCODED)
   @POST
   String postString(@BeanParam FormResourceClientForm form);

}
