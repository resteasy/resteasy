package org.jboss.resteasy.test.client.proxy.resource;

import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;

import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

/**
 * Created by Marek Marusic <mmarusic@redhat.com> on 3/4/19.
 */
@Path("/a")
public interface ProxyBeanParam {

    @Path("a/{p1}/{p2}/{p3}")
    @GET
    String getAll(@BeanParam Params beanParam, @PathParam String p2, @QueryParam String queryParam);
}
