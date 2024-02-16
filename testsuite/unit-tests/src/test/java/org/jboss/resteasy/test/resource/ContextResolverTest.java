package org.jboss.resteasy.test.resource;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.ContextResolver;

import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.resource.resource.ContextResolver1;
import org.jboss.resteasy.test.resource.resource.ContextResolver2;
import org.jboss.resteasy.test.resource.resource.ContextResolver3;
import org.jboss.resteasy.test.resource.resource.ContextResolver4;
import org.jboss.resteasy.test.resource.resource.ContextResolver5;
import org.jboss.resteasy.test.resource.resource.ContextResolver6;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * @tpSubChapter Resource
 * @tpChapter Unit tests
 * @tpTestCaseDetails Test for jakarta.ws.rs.ext.ContextResolver class.
 * @tpSince RESTEasy 3.0.16
 */
public class ContextResolverTest {
    @BeforeAll
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
        Assertions.assertEquals(resolver.getContext(int.class), "2", errMsg);
        Assertions.assertEquals(resolver.getContext(float.class), "5", errMsg);
        resolver = factory.getContextResolver(String.class, MediaType.TEXT_XML_TYPE);
        Assertions.assertEquals(resolver.getContext(int.class), "3", errMsg);
        Assertions.assertEquals(resolver.getContext(float.class), "6", errMsg);
        resolver = factory.getContextResolver(String.class, MediaType.APPLICATION_ATOM_XML_TYPE);
        Assertions.assertEquals(resolver.getContext(int.class), "1", errMsg);
        Assertions.assertEquals(resolver.getContext(float.class), "4", errMsg);
        Assertions.assertNull(resolver.getContext(double.class),
                "Unexpected context was returned by ContextResolver");
        Assertions.assertNull(factory.getContextResolver(Double.class, MediaType.APPLICATION_ATOM_XML_TYPE),
                "Unexpected context was returned by ContextResolver");
    }

}
