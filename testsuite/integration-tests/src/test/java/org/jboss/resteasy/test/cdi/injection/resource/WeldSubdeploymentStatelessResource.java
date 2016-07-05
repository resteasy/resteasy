package org.jboss.resteasy.test.cdi.injection.resource;

import org.jboss.resteasy.test.cdi.injection.WeldSubdeploymentTest;
import org.junit.Assert;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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