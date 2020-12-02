package org.jboss.resteasy.test.resource.path.resource;

import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("resource")
public class LocatorWithClassHierarchyLocatorResource extends LocatorWithClassHierarchyMiddleResource {

   @Path("locator/{id1}/{id2}")
   public LocatorWithClassHierarchyMiddleResource locatorHasArguments(@PathParam("id1") String id1,
                                              @PathParam("id2") String id2) {
      return new LocatorWithClassHierarchyMiddleResource(id1, id2);
   }
}
