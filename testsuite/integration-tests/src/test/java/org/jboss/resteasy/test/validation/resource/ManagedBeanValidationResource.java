package org.jboss.resteasy.test.validation.resource;

import java.util.concurrent.atomic.AtomicInteger;

import jakarta.annotation.ManagedBean;
import jakarta.enterprise.inject.Vetoed;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptors;
import jakarta.interceptor.InvocationContext;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("")
@ManagedBean
@Produces("application/json")
@Consumes("application/json")
@Vetoed
public class ManagedBeanValidationResource {

    private static boolean visited = false;

    private static final AtomicInteger INTERCEPTOR_COUNT = new AtomicInteger(0);

    public static class TestInterceptor {
        @AroundInvoke
        public Object around(final InvocationContext context) throws Exception {
            INTERCEPTOR_COUNT.incrementAndGet();
            return context.proceed();
        }
    }

    @GET
    @Path("validate")
    public Response validate(@Min(value = 1) @QueryParam("q") int n) {
        visited = true;
        return Response.status(Status.OK).entity(n).build();
    }

    @GET
    @Path("visited")
    public Response visited() {
        return Response.ok(visited).build();
    }

    @GET
    @Path("intercepted")
    @Interceptors(TestInterceptor.class)
    public Response checkIntercepted() {
        return Response.ok(INTERCEPTOR_COUNT.get()).build();
    }
}
