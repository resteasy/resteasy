package org.jboss.resteasy.test.cdi.basic.resource;


import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import java.lang.reflect.Method;

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
        logger.info("classname: " + fooResource.getClass().getName());
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
