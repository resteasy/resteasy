package org.jboss.resteasy.test.cdi.injection.resource;


import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Logger;

@Path("/reverse")
@RequestScoped
public class ReverseInjectionResource {
    public static final String NON_CONTEXTUAL = "non-contextual";

    static HashMap<String, Object> store = new HashMap<String, Object>();

    @Inject
    int secret;

    @Inject
    private ReverseInjectionEJBHolderLocal holder;

    @Inject
    private Logger log;

    @Inject
    private CDIInjectionBookResource resource;

    @POST
    @Path("testScopes")
    public Response testScopes() {
        log.info("entered ReverseInjectionResource.testScopes()");
        return holder.testScopes() ? Response.ok().build() : Response.serverError().build();
    }

    @POST
    @Path("setup")
    public Response setup() {
        log.info("entered ReverseInjectionResource.setup()");
        store.put("this.secret", this.secret);
        store.put("holder.secret", holder.theSecret());
        store.put("resource.secret", resource.theSecret());
        store.put("resource", resource);
        resource.getSet().add(new CDIInjectionBook("test"));
        holder.setup();
        return Response.ok().build();
    }

    @POST
    @Path("test")
    public Response test() {
        log.info("entered ReverseInjectionResource.test()");
        if (CDIInjectionBookResource.class.cast(store.get("resource")).getSet().size() > 0) {
            Iterator<CDIInjectionBook> it = CDIInjectionBookResource.class.cast(store.get("resource")).getSet().iterator();
            log.info("stored resource set:");
            while (it.hasNext()) {
                log.info("  " + it.next());
            }
            return Response.serverError().entity("stored resource set not empty").build();
        }
        if (secret == Integer.class.cast(store.get("this.secret"))) {
            return Response.serverError().entity("secret == store.get(\"this.secret\") shouldn't be true").build();
        }
        if (holder.theSecret() == (Integer.class.cast(store.get("holder.secret")))) {
            return Response.serverError().entity("holder.theSecret == store.get(\"holder.secret\") shouldn't be true").build();
        }
        if (resource.theSecret() == Integer.class.cast(store.get("resource.secret"))) {
            return Response.serverError().entity("resource.theSecret() == store.get(\"resource.secret\") shouldn't be true").build();
        }
        if (holder.test()) {
            return Response.ok().build();
        } else {
            return Response.serverError().build();
        }
    }
}
