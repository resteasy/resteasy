package org.jboss.resteasy.links.test;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import org.jboss.resteasy.annotations.jaxrs.QueryParam;
import org.jboss.resteasy.links.LinksProvider;
import org.jboss.resteasy.links.RESTServiceDiscovery;

@Path("/")
public class ClassLinksProviderService {

    @GET
    @Produces("application/json")
    public RESTServiceDiscovery getForClass(@QueryParam("className") String className) throws ClassNotFoundException {
        return LinksProvider.getClassLinksProvider().getLinks(Class.forName(className));
    }
}
