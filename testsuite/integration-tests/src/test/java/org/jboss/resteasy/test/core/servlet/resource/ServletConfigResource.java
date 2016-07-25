package org.jboss.resteasy.test.core.servlet.resource;

import org.junit.Assert;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.xml.bind.annotation.XmlRootElement;

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
        Assert.assertNull(foo);
    }
}
