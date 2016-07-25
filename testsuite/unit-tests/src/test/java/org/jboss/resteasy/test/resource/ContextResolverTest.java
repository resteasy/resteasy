package org.jboss.resteasy.test.resource;

import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.resource.resource.ContextResolver1;
import org.jboss.resteasy.test.resource.resource.ContextResolver2;
import org.jboss.resteasy.test.resource.resource.ContextResolver3;
import org.jboss.resteasy.test.resource.resource.ContextResolver4;
import org.jboss.resteasy.test.resource.resource.ContextResolver5;
import org.jboss.resteasy.test.resource.resource.ContextResolver6;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;

/**
 * @tpSubChapter Resource
 * @tpChapter Unit tests
 * @tpTestCaseDetails Test for javax.ws.rs.ext.ContextResolver class.
 * @tpSince RESTEasy 3.0.16
 */
public class ContextResolverTest {
    @BeforeClass
    public static void before() {
        ResteasyProviderFactory factory = ResteasyProviderFactory.getInstance();
        factory.registerProvider(ContextResolver1.class);
        factory.registerProvider(ContextResolver2.class);
        factory.registerProvider(ContextResolver3.class);
        factory.registerProvider(ContextResolver4.class);
        factory.registerProvider(ContextResolver5.class);
        factory.registerProvider(ContextResolver6.class);
    }

    /**
     * @tpTestDetails Test contexts for various types.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testContextResolver() {
        String errMsg = "Failed to get context by ContextResolver";
        ResteasyProviderFactory factory = ResteasyProviderFactory.getInstance();
        ContextResolver<String> resolver = factory.getContextResolver(String.class, MediaType.TEXT_PLAIN_TYPE);
        Assert.assertEquals(errMsg, resolver.getContext(int.class), "2");
        Assert.assertEquals(errMsg, resolver.getContext(float.class), "5");
        resolver = factory.getContextResolver(String.class, MediaType.TEXT_XML_TYPE);
        Assert.assertEquals(errMsg, resolver.getContext(int.class), "3");
        Assert.assertEquals(errMsg, resolver.getContext(float.class), "6");
        resolver = factory.getContextResolver(String.class, MediaType.APPLICATION_ATOM_XML_TYPE);
        Assert.assertEquals(errMsg, resolver.getContext(int.class), "1");
        Assert.assertEquals(errMsg, resolver.getContext(float.class), "4");
        Assert.assertNull("Unexpected context was returned by ContextResolver",
                resolver.getContext(double.class));
        Assert.assertNull("Unexpected context was returned by ContextResolver",
                factory.getContextResolver(Double.class, MediaType.APPLICATION_ATOM_XML_TYPE));
    }

}
