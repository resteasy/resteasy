package org.jboss.resteasy.test.cdi.injection.resource;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.jboss.resteasy.test.cdi.injection.WeldSubdeploymentTest;
import org.junit.jupiter.api.Assertions;

@Path("/stateless")
@Stateless
public class WeldSubdeploymentStatelessResource {

    @Inject
    private WeldSubdeploymentCdiJpaInjectingBean bean;

    private boolean firstAccess = true;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public void getMethod() {
        Assertions.assertNotNull(bean.entityManagerFactory(), WeldSubdeploymentTest.ERROR_MESSAGE);
    }

}
