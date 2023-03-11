package org.jboss.resteasy.test.cdi.extensions.resource;

import java.util.logging.Logger;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.test.cdi.util.Utilities;

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
        log.info("ObsolescentObject scope: " + utilities.testScope(ScopeExtensionObsolescentAfterTwoUses.class,
                ScopeExtensionPlannedObsolescenceScope.class));
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
