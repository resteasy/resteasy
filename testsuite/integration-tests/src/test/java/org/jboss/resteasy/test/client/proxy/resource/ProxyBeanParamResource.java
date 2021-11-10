package org.jboss.resteasy.test.client.proxy.resource;

import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;

/**
 * Created by Marek Marusic <mmarusic@redhat.com> on 3/4/19.
 */
@Path("/a")
public class ProxyBeanParamResource {

    @GET
    @Path("a/{p1}/{p2}/{p3}")
    public String getAll(@BeanParam Params beanParam, @PathParam String p2, @QueryParam String queryParam) {
        return beanParam.getP1() + "_" + p2 + "_" + beanParam.getP3() + "_" + beanParam.getQ1() + "_" + queryParam;
    }
}
