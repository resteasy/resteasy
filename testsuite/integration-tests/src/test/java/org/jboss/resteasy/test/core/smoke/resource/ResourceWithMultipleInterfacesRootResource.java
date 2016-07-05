package org.jboss.resteasy.test.core.smoke.resource;


import javax.ws.rs.Path;

@Path("/")
public class ResourceWithMultipleInterfacesRootResource implements ResourceWithMultipleInterfacesIntA, ResourceWithMultipleInterfacesEmpty {
    @Override
    public String getFoo() {
        return "FOO";
    }
}
