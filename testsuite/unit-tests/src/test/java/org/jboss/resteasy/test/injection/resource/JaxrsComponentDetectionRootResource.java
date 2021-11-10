package org.jboss.resteasy.test.injection.resource;

import jakarta.ws.rs.Path;

@Path("resource")
public class JaxrsComponentDetectionRootResource implements JaxrsComponentDetectionSubresourceLocal {
   public void foo() {
   }
}
