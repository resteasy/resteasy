package org.jboss.resteasy.test.form.resource;

import javax.ws.rs.BeanParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

@Path("/form/{id}")
public interface FormResourceClientProxy {
    @Produces(MediaType.APPLICATION_FORM_URLENCODED)
    @POST
    MultivaluedMap<String, String> post(@BeanParam FormResourceClientForm form);

    @Produces(MediaType.APPLICATION_FORM_URLENCODED)
    @POST
    String postString(@BeanParam FormResourceClientForm form);

}
