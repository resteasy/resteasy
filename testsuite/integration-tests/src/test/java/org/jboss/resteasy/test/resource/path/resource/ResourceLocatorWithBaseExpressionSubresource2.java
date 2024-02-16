package org.jboss.resteasy.test.resource.path.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;

import org.junit.jupiter.api.Assertions;

public class ResourceLocatorWithBaseExpressionSubresource2 {
    @GET
    @Path("stuff/{param}/bar")
    public String doGet(@PathParam("param") String param, @Context UriInfo uri) {
        Assertions.assertEquals(4, uri.getMatchedURIs().size(),
                ResourceLocatorWithBaseExpressionResource.ERROR_MSG);
        Assertions.assertEquals("a1/base/1/resources/subresource2/stuff/2/bar",
                uri.getMatchedURIs().get(0),
                ResourceLocatorWithBaseExpressionResource.ERROR_MSG);
        Assertions.assertEquals("a1/base/1/resources/subresource2",
                uri.getMatchedURIs().get(1), ResourceLocatorWithBaseExpressionResource.ERROR_MSG);
        Assertions.assertEquals("a1/base/1/resources",
                uri.getMatchedURIs().get(2), ResourceLocatorWithBaseExpressionResource.ERROR_MSG);
        Assertions.assertEquals("a1", uri.getMatchedURIs().get(3),
                ResourceLocatorWithBaseExpressionResource.ERROR_MSG);

        Assertions.assertEquals(3, uri.getMatchedResources().size(),
                ResourceLocatorWithBaseExpressionResource.ERROR_MSG);
        Assertions.assertEquals(ResourceLocatorWithBaseExpressionSubresource2.class,
                uri.getMatchedResources().get(0).getClass(),
                ResourceLocatorWithBaseExpressionResource.ERROR_MSG);
        Assertions.assertEquals(ResourceLocatorWithBaseExpressionSubresource.class, uri.getMatchedResources().get(1).getClass(),
                ResourceLocatorWithBaseExpressionResource.ERROR_MSG);
        Assertions.assertEquals(ResourceLocatorWithBaseExpressionResource.class, uri.getMatchedResources().get(2).getClass(),
                ResourceLocatorWithBaseExpressionResource.ERROR_MSG);
        Assertions.assertEquals("2", param);
        return this.getClass().getName() + "-" + param;
    }
}
