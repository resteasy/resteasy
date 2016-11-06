package org.jboss.resteasy.test.cdi.extensions.resource;


import org.jboss.resteasy.test.cdi.util.Utilities;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;

@Path("/extension")
@RequestScoped
public class ScopeExtensionResource {
    private static int lastSecret2;
    private static int lastSecret3;

    @Inject
    private Logger log;
    @Inject
    private Utilities utilities;
    @Inject
    private ScopeExtensionObsolescentAfterTwoUses oo2;
    @Inject
    private ScopeExtensionObsolescentAfterThreeUses oo3;

    @POST
    @Path("setup")
    public Response setup() {
        log.info("ObsolescentObject scope: " + utilities.testScope(ScopeExtensionObsolescentAfterTwoUses.class, ScopeExtensionPlannedObsolescenceScope.class));
        if (utilities.testScope(ScopeExtensionObsolescentAfterTwoUses.class, ScopeExtensionPlannedObsolescenceScope.class)) {
            lastSecret2 = oo2.getSecret();
            lastSecret3 = oo3.getSecret();
            log.info("current secret2: " + lastSecret2);
            log.info("current secret3: " + lastSecret3);
            return Response.ok().build();
        } else {
            return Response.serverError().build();
        }
    }

    @POST
    @Path("test1")
    public Response test1() {
        int currentSecret2 = oo2.getSecret();
        int currentSecret3 = oo3.getSecret();
        log.info("last secret2:    " + lastSecret2);
        log.info("last secret3:    " + lastSecret3);
        log.info("current secret2: " + currentSecret2);
        log.info("current secret3: " + currentSecret3);
        if (currentSecret2 == lastSecret2 && currentSecret3 == lastSecret3) {
            lastSecret2 = currentSecret2;
            lastSecret3 = currentSecret3;
            return Response.ok().build();
        } else {
            return Response.serverError().build();
        }
    }

    @POST
    @Path("test2")
    public Response test2() {
        int currentSecret2 = oo2.getSecret();
        int currentSecret3 = oo3.getSecret();
        log.info("last secret2:    " + lastSecret2);
        log.info("last secret3:    " + lastSecret3);
        log.info("current secret2: " + currentSecret2);
        log.info("current secret3: " + currentSecret3);
        if (currentSecret2 != lastSecret2 && currentSecret3 == lastSecret3) {
            lastSecret3 = currentSecret3;
            return Response.ok().build();
        } else {
            return Response.serverError().build();
        }
    }

    @POST
    @Path("test3")
    public Response test3() {
        int currentSecret3 = oo3.getSecret();
        log.info("last secret3:    " + lastSecret3);
        log.info("current secret3: " + currentSecret3);
        if (currentSecret3 != lastSecret3) {
            return Response.ok().build();
        } else {
            return Response.serverError().build();
        }
    }
}
