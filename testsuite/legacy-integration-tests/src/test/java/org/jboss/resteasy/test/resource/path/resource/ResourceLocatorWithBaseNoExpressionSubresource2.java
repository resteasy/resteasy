package org.jboss.resteasy.test.resource.path.resource;

import org.junit.Assert;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

public class ResourceLocatorWithBaseNoExpressionSubresource2 {
    @GET
    @Path("stuff/{param}/bar")
    public String doGet(@PathParam("param") String param, @Context UriInfo uri) {
        Assert.assertEquals(ResourceLocatorWithBaseNoExpressionResource.ERROR_MSG, 4, uri.getMatchedURIs().size());
        Assert.assertEquals(ResourceLocatorWithBaseNoExpressionResource.ERROR_MSG, "a1/base/1/resources/subresource2/stuff/2/bar", uri.getMatchedURIs().get(0));
        Assert.assertEquals(ResourceLocatorWithBaseNoExpressionResource.ERROR_MSG, "a1/base/1/resources/subresource2", uri.getMatchedURIs().get(1));
        Assert.assertEquals(ResourceLocatorWithBaseNoExpressionResource.ERROR_MSG, "a1/base/1/resources", uri.getMatchedURIs().get(2));
        Assert.assertEquals(ResourceLocatorWithBaseNoExpressionResource.ERROR_MSG, "a1", uri.getMatchedURIs().get(3));

        Assert.assertEquals(ResourceLocatorWithBaseNoExpressionResource.ERROR_MSG, 3, uri.getMatchedResources().size());
        Assert.assertEquals(ResourceLocatorWithBaseNoExpressionResource.ERROR_MSG, ResourceLocatorWithBaseNoExpressionSubresource2.class, uri.getMatchedResources().get(0).getClass());
        Assert.assertEquals(ResourceLocatorWithBaseNoExpressionResource.ERROR_MSG, ResourceLocatorWithBaseNoExpressionSubresource.class, uri.getMatchedResources().get(1).getClass());
        Assert.assertEquals(ResourceLocatorWithBaseNoExpressionResource.ERROR_MSG, ResourceLocatorWithBaseNoExpressionResource.class, uri.getMatchedResources().get(2).getClass());
        Assert.assertEquals("2", param);
        return this.getClass().getName() + "-" + param;
    }
}
