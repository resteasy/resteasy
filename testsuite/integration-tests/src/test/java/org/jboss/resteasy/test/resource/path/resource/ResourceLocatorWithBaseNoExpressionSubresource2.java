package org.jboss.resteasy.test.resource.path.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;

import org.junit.jupiter.api.Assertions;

public class ResourceLocatorWithBaseNoExpressionSubresource2 {
    @GET
    @Path("stuff/{param}/bar")
    public String doGet(@PathParam("param") String param, @Context UriInfo uri) {
        Assertions.assertEquals(4, uri.getMatchedURIs().size(),
                ResourceLocatorWithBaseNoExpressionResource.ERROR_MSG);
        Assertions.assertEquals("a1/base/1/resources/subresource2/stuff/2/bar", uri.getMatchedURIs().get(0),
                ResourceLocatorWithBaseNoExpressionResource.ERROR_MSG);
        Assertions.assertEquals("a1/base/1/resources/subresource2",
                uri.getMatchedURIs().get(1),
                ResourceLocatorWithBaseNoExpressionResource.ERROR_MSG);
        Assertions.assertEquals("a1/base/1/resources",
                uri.getMatchedURIs().get(2),
                ResourceLocatorWithBaseNoExpressionResource.ERROR_MSG);
        Assertions.assertEquals("a1", uri.getMatchedURIs().get(3),
                ResourceLocatorWithBaseNoExpressionResource.ERROR_MSG);

        Assertions.assertEquals(3, uri.getMatchedResources().size(),
                ResourceLocatorWithBaseNoExpressionResource.ERROR_MSG);
        Assertions.assertEquals(ResourceLocatorWithBaseNoExpressionSubresource2.class,
                uri.getMatchedResources().get(0).getClass(),
                ResourceLocatorWithBaseNoExpressionResource.ERROR_MSG);
        Assertions.assertEquals(ResourceLocatorWithBaseNoExpressionSubresource.class,
                uri.getMatchedResources().get(1).getClass(),
                ResourceLocatorWithBaseNoExpressionResource.ERROR_MSG);
        Assertions.assertEquals(ResourceLocatorWithBaseNoExpressionResource.class, uri.getMatchedResources().get(2).getClass(),
                ResourceLocatorWithBaseNoExpressionResource.ERROR_MSG);
        Assertions.assertEquals("2", param);
        return this.getClass().getName() + "-" + param;
    }
}
