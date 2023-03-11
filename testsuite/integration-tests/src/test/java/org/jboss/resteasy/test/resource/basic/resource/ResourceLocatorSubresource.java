package org.jboss.resteasy.test.resource.basic.resource;

import java.util.List;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;

import org.jboss.logging.Logger;
import org.junit.Assert;

public class ResourceLocatorSubresource {

    private static final Logger LOG = Logger.getLogger(ResourceLocatorSubresource.class);

    @GET
    public String doGet(@Context UriInfo uri) {
        LOG.info("Uri Ancesstors for Subresource.doGet():");
        List<String> matchedURIs = uri.getMatchedURIs();
        Assert.assertEquals(2, matchedURIs.size());
        Assert.assertEquals("base/1/resources", matchedURIs.get(0));
        Assert.assertEquals("", matchedURIs.get(1));
        for (String ancestor : matchedURIs)
            LOG.info("   " + ancestor);

        LOG.info("Uri Ancesstors Object for Subresource.doGet():");
        Assert.assertEquals(2, uri.getMatchedResources().size());
        Assert.assertEquals(ResourceLocatorSubresource.class, uri.getMatchedResources().get(0).getClass());
        Assert.assertEquals(ResourceLocatorBaseResource.class, uri.getMatchedResources().get(1).getClass());
        for (Object ancestor : uri.getMatchedResources())
            LOG.infov("   {0}", ancestor.getClass().getName());
        return this.getClass().getName();
    }

    @Path("/subresource2")
    public Object getSubresource2(@Context UriInfo uri) {
        LOG.info("Uri Ancesstors for Subresource.getSubresource2():");
        List<String> matchedURIs = uri.getMatchedURIs();
        Assert.assertEquals(3, matchedURIs.size());
        Assert.assertEquals("base/1/resources/subresource2", matchedURIs.get(0));
        Assert.assertEquals("base/1/resources", matchedURIs.get(1));
        Assert.assertEquals("", matchedURIs.get(2));
        for (String ancestor : matchedURIs)
            LOG.info("   " + ancestor);

        LOG.info("Uri Ancesstors Object for Subresource.getSubresource2():");
        Assert.assertEquals(2, uri.getMatchedResources().size());
        Assert.assertEquals(ResourceLocatorSubresource.class, uri.getMatchedResources().get(0).getClass());
        Assert.assertEquals(ResourceLocatorBaseResource.class, uri.getMatchedResources().get(1).getClass());
        for (Object ancestor : uri.getMatchedResources())
            LOG.infov("   {0}", ancestor.getClass().getName());
        return new ResourceLocatorSubresource2();
    }
}
