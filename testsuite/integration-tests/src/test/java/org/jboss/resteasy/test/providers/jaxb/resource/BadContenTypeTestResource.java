package org.jboss.resteasy.test.providers.jaxb.resource;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/test")
public class BadContenTypeTestResource {

    @GET
    public Response get() {
        BadContentTypeTestBean bean = new BadContentTypeTestBean();
        bean.setName("myname");
        return Response.ok(bean).build();
    }

    @GET
    @Produces("text/html")
    @Path("foo")
    public Response getMissingMBW() {
        BadContentTypeTestBean bean = new BadContentTypeTestBean();
        bean.setName("myname");
        return Response.ok(bean).build();
    }

    @POST
    public void post(BadContentTypeTestBean bean) {

    }

}
