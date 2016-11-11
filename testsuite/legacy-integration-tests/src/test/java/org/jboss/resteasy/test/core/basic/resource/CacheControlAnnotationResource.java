package org.jboss.resteasy.test.core.basic.resource;

import org.jboss.resteasy.annotations.cache.Cache;
import org.jboss.resteasy.annotations.cache.NoCache;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/")
public class CacheControlAnnotationResource {
    @GET
    @Cache(maxAge = 3600)
    @Path("/maxage")
    public String getMaxAge() {
        return "maxage";
    }

    @GET
    @NoCache
    @Path("nocache")
    public String getNoCache() {
        return "nocache";
    }

}
