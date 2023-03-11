package org.jboss.resteasy.test.core.basic.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

import org.jboss.resteasy.annotations.cache.Cache;
import org.jboss.resteasy.annotations.cache.NoCache;

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

    @GET
    @Cache(maxAge = 0, sMaxAge = 0, mustRevalidate = true, noCache = true, noStore = true, isPrivate = true)
    @Path("composite")
    public String getCompositeDirectives() {
        return "compositeDirectives";
    }
}
