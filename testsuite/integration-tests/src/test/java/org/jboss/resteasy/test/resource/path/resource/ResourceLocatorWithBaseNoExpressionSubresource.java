package org.jboss.resteasy.test.resource.path.resource;

import java.util.List;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;

import org.junit.Assert;

public class ResourceLocatorWithBaseNoExpressionSubresource {

    @GET
    public String doGet(@Context UriInfo uri) {
        List<String> matchedURIs = uri.getMatchedURIs();
        Assert.assertEquals(ResourceLocatorWithBaseNoExpressionResource.ERROR_MSG, 2, matchedURIs.size());
        Assert.assertEquals(ResourceLocatorWithBaseNoExpressionResource.ERROR_MSG, "a1/base/1/resources", matchedURIs.get(0));
        Assert.assertEquals(ResourceLocatorWithBaseNoExpressionResource.ERROR_MSG, "a1", matchedURIs.get(1));

        Assert.assertEquals(ResourceLocatorWithBaseNoExpressionResource.ERROR_MSG, 2, uri.getMatchedResources().size());
        Assert.assertEquals(ResourceLocatorWithBaseNoExpressionResource.ERROR_MSG,
                ResourceLocatorWithBaseNoExpressionSubresource.class, uri.getMatchedResources().get(0).getClass());
        Assert.assertEquals(ResourceLocatorWithBaseNoExpressionResource.ERROR_MSG,
                ResourceLocatorWithBaseNoExpressionResource.class, uri.getMatchedResources().get(1).getClass());
        return this.getClass().getName();
    }

    @Path("/subresource2")
    public Object getSubresource2(@Context UriInfo uri) {
        Assert.assertEquals(ResourceLocatorWithBaseNoExpressionResource.ERROR_MSG, 3, uri.getMatchedURIs().size());
        Assert.assertEquals(ResourceLocatorWithBaseNoExpressionResource.ERROR_MSG, "a1/base/1/resources/subresource2",
                uri.getMatchedURIs().get(0));
        Assert.assertEquals(ResourceLocatorWithBaseNoExpressionResource.ERROR_MSG, "a1/base/1/resources",
                uri.getMatchedURIs().get(1));
        Assert.assertEquals(ResourceLocatorWithBaseNoExpressionResource.ERROR_MSG, "a1", uri.getMatchedURIs().get(2));
        Assert.assertEquals(ResourceLocatorWithBaseNoExpressionResource.ERROR_MSG, 2, uri.getMatchedResources().size());
        Assert.assertEquals(ResourceLocatorWithBaseNoExpressionResource.ERROR_MSG,
                ResourceLocatorWithBaseNoExpressionSubresource.class, uri.getMatchedResources().get(0).getClass());
        Assert.assertEquals(ResourceLocatorWithBaseNoExpressionResource.ERROR_MSG,
                ResourceLocatorWithBaseNoExpressionResource.class, uri.getMatchedResources().get(1).getClass());
        return new ResourceLocatorWithBaseNoExpressionSubresource2();
    }
}
