package org.jboss.resteasy.test.resource.param.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("test")
public class SerializableWithParametersResource {
    @GET
    @SuppressWarnings("deprecation")
    public Response test() {
        return Response.ok().entity(new SerializableWithParametersObject("abc")).type(org.jboss.resteasy.plugins.providers.SerializableProvider.APPLICATION_SERIALIZABLE + ";q=0.5").build();
    }
}
