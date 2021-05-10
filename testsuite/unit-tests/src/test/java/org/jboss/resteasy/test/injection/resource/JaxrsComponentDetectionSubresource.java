package org.jboss.resteasy.test.injection.resource;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class JaxrsComponentDetectionSubresource implements JaxrsComponentDetectionSubresourceLocal {
   public void foo() {
   }
}
