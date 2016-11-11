package org.jboss.resteasy.test.providers.jaxb.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Path("enum")
public class XmlEnumParamResource {
    @GET
    @Produces("text/plain")
    public String get(@QueryParam("loc") XmlEnumParamLocation loc) {
        return loc.toString();
    }
}
