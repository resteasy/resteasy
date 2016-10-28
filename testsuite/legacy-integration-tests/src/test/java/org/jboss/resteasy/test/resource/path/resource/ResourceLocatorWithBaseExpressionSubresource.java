package org.jboss.resteasy.test.resource.path.resource;

import org.junit.Assert;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import java.util.List;

public class ResourceLocatorWithBaseExpressionSubresource {

    @GET
    public String doGet(@Context UriInfo uri) {
        List<String> matchedURIs = uri.getMatchedURIs();
        Assert.assertEquals(ResourceLocatorWithBaseExpressionResource.ERROR_MSG, 2, matchedURIs.size());
        Assert.assertEquals(ResourceLocatorWithBaseExpressionResource.ERROR_MSG, "a1/base/1/resources", matchedURIs.get(0));
        Assert.assertEquals(ResourceLocatorWithBaseExpressionResource.ERROR_MSG, "a1", matchedURIs.get(1));

        Assert.assertEquals(ResourceLocatorWithBaseExpressionResource.ERROR_MSG, 2, uri.getMatchedResources().size());
        Assert.assertEquals(ResourceLocatorWithBaseExpressionResource.ERROR_MSG, ResourceLocatorWithBaseExpressionSubresource.class, uri.getMatchedResources().get(0).getClass());
        Assert.assertEquals(ResourceLocatorWithBaseExpressionResource.ERROR_MSG, ResourceLocatorWithBaseExpressionResource.class, uri.getMatchedResources().get(1).getClass());
        return this.getClass().getName();
    }

    @Path("/subresource2")
    public Object getSubresource2(@Context UriInfo uri) {
        Assert.assertEquals(ResourceLocatorWithBaseExpressionResource.ERROR_MSG, 3, uri.getMatchedURIs().size());
        Assert.assertEquals(ResourceLocatorWithBaseExpressionResource.ERROR_MSG, "a1/base/1/resources/subresource2", uri.getMatchedURIs().get(0));
        Assert.assertEquals(ResourceLocatorWithBaseExpressionResource.ERROR_MSG, "a1/base/1/resources", uri.getMatchedURIs().get(1));
        Assert.assertEquals(ResourceLocatorWithBaseExpressionResource.ERROR_MSG, "a1", uri.getMatchedURIs().get(2));
        Assert.assertEquals(ResourceLocatorWithBaseExpressionResource.ERROR_MSG, 2, uri.getMatchedResources().size());
        Assert.assertEquals(ResourceLocatorWithBaseExpressionResource.ERROR_MSG, ResourceLocatorWithBaseExpressionSubresource.class, uri.getMatchedResources().get(0).getClass());
        Assert.assertEquals(ResourceLocatorWithBaseExpressionResource.ERROR_MSG, ResourceLocatorWithBaseExpressionResource.class, uri.getMatchedResources().get(1).getClass());
        return new ResourceLocatorWithBaseExpressionSubresource2();
    }
}
