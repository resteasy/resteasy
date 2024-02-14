package org.jboss.resteasy.test.core.servlet.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.junit.jupiter.api.Assertions;

@Path("my")
public class ServletConfigResource {
    @XmlRootElement
    public static class Foo {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

    public static int num_instatiations = 0;

    @Path("count")
    @GET
    @Produces("text/plain")
    public String getCount() {
        return Integer.toString(num_instatiations);
    }

    @Path("application/count")
    @GET
    @Produces("text/plain")
    public String getApplicationCount() {
        return Integer.toString(ServletConfigApplication.num_instantiations);
    }

    @Path("exception")
    @GET
    @Produces("text/plain")
    public String getException() {
        throw new ServletConfigException();
    }

    @Path("null")
    @POST
    @Consumes("application/xml")
    public void nullFoo(Foo foo) {
        Assertions.assertNull(foo);
    }
}
