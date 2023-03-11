package org.jboss.resteasy.test.core.basic.resource;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;

import org.jboss.resteasy.spi.HttpRequest;
import org.junit.Assert;

@Path("/")
public class ContextService {
    @GET
    @Path("/forward")
    public void forward(@Context HttpRequest request) {
        request.forward("/WEB-INF/index.txt");
    }

    @GET
    @Path("/test")
    @Produces("text/plain")
    public String get(@Context UriInfo uri) {
        return uri.getBaseUri().toString();
    }

    @GET
    @Produces("text/plain")
    public String getBase(@Context UriInfo uri) {
        return uri.getBaseUri().toString();
    }

    @GET
    @Path("/test/servletcontext")
    @Produces("text/plain")
    public String get(@Context ServletContext context) {
        Assert.assertNotNull("ServletConfig was not injected", context);
        return "ok";
    }

    @GET
    @Path("/test/servletconfig")
    @Produces("text/plain")
    public String get(@Context ServletConfig config) {
        Assert.assertNotNull("ServletConfig was not injected", config);
        Assert.assertNotNull("ServletConfig don't contain servlet.greeting parameter",
                config.getInitParameter("servlet.greeting"));
        Assert.assertTrue("ServletConfig has some unexpected parameters", config.getInitParameterNames().hasMoreElements());
        return "ok";
    }

    @GET
    @Produces("application/json")
    @Path("stuff")
    public String getJson() {
        return "json";
    }

    @GET
    @Produces("application/xml")
    @Path("stuff")
    public String getXml() {
        return "xml";
    }
}
