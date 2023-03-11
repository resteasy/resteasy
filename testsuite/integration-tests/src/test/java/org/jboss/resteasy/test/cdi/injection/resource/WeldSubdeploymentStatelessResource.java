package org.jboss.resteasy.test.cdi.injection.resource;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.jboss.resteasy.test.cdi.injection.WeldSubdeploymentTest;
import org.junit.Assert;

@Path("/stateless")
@Stateless
public class WeldSubdeploymentStatelessResource {

    @Inject
    private WeldSubdeploymentCdiJpaInjectingBean bean;

    private boolean firstAccess = true;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public void getMethod() {
        Assert.assertNotNull(WeldSubdeploymentTest.ERROR_MESSAGE, bean.entityManagerFactory());
    }

}
