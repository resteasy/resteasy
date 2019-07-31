package org.jboss.resteasy.spring.web;

import org.springframework.http.ResponseEntity;

import javax.ws.rs.ConstrainedTo;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

@Provider
@ConstrainedTo(RuntimeType.SERVER)
public class ResponseEntityFeature implements DynamicFeature {

    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext context) {
        if (!ResponseEntity.class.equals(resourceInfo.getResourceMethod().getReturnType())) {
            return;
        }

        context.register(new ResponseEntityContainerResponseFilter());
    }
}
