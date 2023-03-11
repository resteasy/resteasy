package org.jboss.resteasy.test.cdi.basic.resource;

import java.lang.reflect.Method;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;

import org.jboss.logging.Logger;

@Path("/test")
@ApplicationScoped // Weld uses a proxy
public class CDILocatorResource {

    private static Logger logger = Logger.getLogger(CDILocatorResource.class);

    @Inject
    FooResource fooResource;

    @GET
    public String get(@QueryParam("foo") java.util.List<Foo> foos) {
        // Bug: It's not a List<Foo>, it's a List<String>
        for (Foo foo : foos) {
            return "OK";
        }
        return null;

    }

    @Path("lookup")
    public FooResource lookup() throws Exception {
        logger.infov("classname: {0}", fooResource.getClass().getName());
        for (Method m : fooResource.getClass().getMethods()) {
            if (m.getName().equals("get")) {
                logger.info(m);
                logger.info("@GET? " + m.isAnnotationPresent(GET.class));
            }
        }
        return fooResource;
    }

    @ApplicationScoped // Weld uses a proxy
    public static class FooResource {

        @GET
        public String get(@QueryParam("foo") java.util.List<Foo> foos) {
            // Bug: It's not a List<Foo>, it's a List<String>
            for (Foo foo : foos) {
                return "OK";
            }
            return null;
        }

    }

    public static class Foo {
        String value;

        public Foo(final String value) {
            this.value = value;
        }
    }
}
