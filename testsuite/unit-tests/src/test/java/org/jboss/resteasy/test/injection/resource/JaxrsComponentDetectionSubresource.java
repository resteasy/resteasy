package org.jboss.resteasy.test.injection.resource;

import javax.enterprise.context.RequestScoped;

@RequestScoped
public class JaxrsComponentDetectionSubresource implements JaxrsComponentDetectionSubresourceLocal {
    public void foo() {
    }
}
