package org.jboss.resteasy.test.interceptor.resource;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.test.interceptor.resource.AddDynamicFeature.DoNothingMethodScopedRequestFilter;
import org.jboss.resteasy.test.interceptor.resource.AddFeature.DoNothingGlobalRequestFilter;

@Path("/dynamic-feature")
public class DynamicFeatureResource {

    @Context
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
