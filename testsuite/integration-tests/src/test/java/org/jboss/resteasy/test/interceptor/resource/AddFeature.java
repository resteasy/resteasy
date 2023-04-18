package org.jboss.resteasy.test.interceptor.resource;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

@Provider
public class AddFeature implements Feature {

    public static final String PROPERTY = "FeatureProperty";
    public static final String PROPERTY_VALUE = "FeaturePropertyValue";

    public static final class DoNothingGlobalRequestFilter implements ContainerRequestFilter {

        @Override
        public void filter(ContainerRequestContext requestContext) throws IOException {
        }

    }

    @Override
    public boolean configure(FeatureContext context) {
        context.property(PROPERTY, PROPERTY_VALUE);
        context.register(DoNothingGlobalRequestFilter.class);
        return true;
    }

}
