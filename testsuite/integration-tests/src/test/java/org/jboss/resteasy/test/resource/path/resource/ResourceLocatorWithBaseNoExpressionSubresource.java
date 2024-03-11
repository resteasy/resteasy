package org.jboss.resteasy.test.resource.path.resource;

import java.util.List;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;

import org.junit.jupiter.api.Assertions;

public class ResourceLocatorWithBaseNoExpressionSubresource {

    @GET
    public String doGet(@Context UriInfo uri) {
        List<String> matchedURIs = uri.getMatchedURIs();
        Assertions.assertEquals(2, matchedURIs.size(), ResourceLocatorWithBaseNoExpressionResource.ERROR_MSG);
        Assertions.assertEquals("a1/base/1/resources", matchedURIs.get(0),
                ResourceLocatorWithBaseNoExpressionResource.ERROR_MSG);
        Assertions.assertEquals("a1", matchedURIs.get(1), ResourceLocatorWithBaseNoExpressionResource.ERROR_MSG);

        Assertions.assertEquals(2, uri.getMatchedResources().size(), ResourceLocatorWithBaseNoExpressionResource.ERROR_MSG);
        Assertions.assertEquals(ResourceLocatorWithBaseNoExpressionSubresource.class,
                uri.getMatchedResources().get(0).getClass(),
                ResourceLocatorWithBaseNoExpressionResource.ERROR_MSG);
        Assertions.assertEquals(ResourceLocatorWithBaseNoExpressionResource.class, uri.getMatchedResources().get(1).getClass(),
                ResourceLocatorWithBaseNoExpressionResource.ERROR_MSG);
        return this.getClass().getName();
    }

    @Path("/subresource2")
    public Object getSubresource2(@Context UriInfo uri) {
        Assertions.assertEquals(3, uri.getMatchedURIs().size(), ResourceLocatorWithBaseNoExpressionResource.ERROR_MSG);
        Assertions.assertEquals("a1/base/1/resources/subresource2", uri.getMatchedURIs().get(0),
                ResourceLocatorWithBaseNoExpressionResource.ERROR_MSG);
        Assertions.assertEquals("a1/base/1/resources", uri.getMatchedURIs().get(1),
                ResourceLocatorWithBaseNoExpressionResource.ERROR_MSG);
        Assertions.assertEquals("a1", uri.getMatchedURIs().get(2), ResourceLocatorWithBaseNoExpressionResource.ERROR_MSG);
        Assertions.assertEquals(2, uri.getMatchedResources().size(), ResourceLocatorWithBaseNoExpressionResource.ERROR_MSG);
        Assertions.assertEquals(ResourceLocatorWithBaseNoExpressionSubresource.class,
                uri.getMatchedResources().get(0).getClass(),
                ResourceLocatorWithBaseNoExpressionResource.ERROR_MSG);
        Assertions.assertEquals(ResourceLocatorWithBaseNoExpressionResource.class, uri.getMatchedResources().get(1).getClass(),
                ResourceLocatorWithBaseNoExpressionResource.ERROR_MSG);
        return new ResourceLocatorWithBaseNoExpressionSubresource2();
    }
}
