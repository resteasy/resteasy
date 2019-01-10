package org.jboss.resteasy.test.resource.resource;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Path;

@Path("/headertest")
public class SimpleHeaderDelegateAsProviderResource {
    @GET
    public String testClient(@DefaultValue("defaultHead")
            @HeaderParam("HeaderTest") SimpleHeaderDelegateAsProviderHeader header) {
        return header.getMajor() + "|" + header.getMinor();
    }
}
