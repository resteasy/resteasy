package org.jboss.resteasy.test.interceptor.resource;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

import org.jboss.logging.Logger;

@Provider
public class AddDynamicFeature implements DynamicFeature {

    private static final Logger LOG = Logger.getLogger(AddDynamicFeature.class.getName());

    public static final String PROPERTY = "DynamicFeatureProperty";
    public static final String PROPERTY_VALUE = "DynamicFeaturePropertyValue";

    public static final class DoNothingMethodScopedRequestFilter implements ContainerRequestFilter {

        @Override
        public void filter(ContainerRequestContext requestContext) throws IOException {
        }

    }

    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext context) {
        String name = resourceInfo.getResourceMethod().getName();
        if (name.equals("hello")) {
            context.register(GreetingInterceptor.class);
            LOG.info("This should be happening exactly once");
        } else if (name.equals("getSpecificMethodContext")) {
            context.property(PROPERTY, PROPERTY_VALUE);
            context.register(DoNothingMethodScopedRequestFilter.class);
        }
    }

}
