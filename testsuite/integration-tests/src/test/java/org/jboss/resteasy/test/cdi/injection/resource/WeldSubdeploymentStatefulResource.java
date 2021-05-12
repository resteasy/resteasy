package org.jboss.resteasy.test.cdi.injection.resource;

import org.jboss.resteasy.test.cdi.injection.WeldSubdeploymentTest;
import org.junit.Assert;

import jakarta.ejb.Stateful;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/stateful")
@Stateful
public class WeldSubdeploymentStatefulResource {

   @Inject
   private WeldSubdeploymentCdiJpaInjectingBean bean;

   @GET
   @Produces(MediaType.TEXT_PLAIN)
   public void getMethod() {
      Assert.assertNotNull(WeldSubdeploymentTest.ERROR_MESSAGE, bean.entityManagerFactory());
   }

}
