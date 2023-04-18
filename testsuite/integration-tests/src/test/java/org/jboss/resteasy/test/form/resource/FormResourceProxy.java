package org.jboss.resteasy.test.form.resource;

import javax.ws.rs.BeanParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/myform")
public interface FormResourceProxy {
    @POST
    void post(@BeanParam FormResourceClientFormSecond form2);
}
