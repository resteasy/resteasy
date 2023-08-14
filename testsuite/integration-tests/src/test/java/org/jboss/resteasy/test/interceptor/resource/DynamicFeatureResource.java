package org.jboss.resteasy.test.interceptor.resource;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.MediaType;

import org.jboss.resteasy.test.annotations.FollowUpRequired;
import org.jboss.resteasy.test.interceptor.resource.AddDynamicFeature.DoNothingMethodScopedRequestFilter;
import org.jboss.resteasy.test.interceptor.resource.AddFeature.DoNothingGlobalRequestFilter;

@Path("/dynamic-feature")
@RequestScoped
@FollowUpRequired("The @RequestScope annotation can be removed once @Path is considered a bean defining annotation.")
public class DynamicFeatureResource {

    @Inject
    private Configuration configuration;

    @Path("/hello")
    @GET
    @POST
    @Produces("text/plain")
    @Consumes("text/plain")
    public String hello(String name) {
        return name;
    }

    @Path("getSpecificMethodContext")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> getSpecificMethodContext() {
        return getOtherMethodContext();
    }

    @Path("getOtherMethodContext")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> getOtherMethodContext() {

        Map<String, Object> result = new HashMap<>();

        Map<String, Object> properties = configuration.getProperties();

        if (properties.containsKey(AddFeature.PROPERTY)) {
            result.put(AddFeature.PROPERTY, properties.get(AddFeature.PROPERTY));
        }
        if (properties.containsKey(AddDynamicFeature.PROPERTY)) {
            result.put(AddDynamicFeature.PROPERTY, properties.get(AddDynamicFeature.PROPERTY));
        }

        Set<?> classes = configuration.getClasses();

        result.put(DoNothingGlobalRequestFilter.class.getCanonicalName(),
                classes.contains(DoNothingGlobalRequestFilter.class));
        result.put(DoNothingMethodScopedRequestFilter.class.getCanonicalName(),
                classes.contains(DoNothingMethodScopedRequestFilter.class));

        return result;
    }

}
