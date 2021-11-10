package org.jboss.resteasy.test.providers.jaxb.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;

@Path("enum")
public class XmlEnumParamResource {
   @GET
   @Produces("text/plain")
   public String get(@QueryParam("loc") XmlEnumParamLocation loc) {
      return loc.toString();
   }
}
