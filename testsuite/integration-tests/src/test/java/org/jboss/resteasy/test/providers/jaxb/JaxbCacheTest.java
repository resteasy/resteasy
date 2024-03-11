package org.jboss.resteasy.test.providers.jaxb;

import java.lang.reflect.ReflectPermission;
import java.util.PropertyPermission;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Providers;
import jakarta.xml.bind.JAXBContext;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.plugins.providers.jaxb.JAXBContextFinder;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.providers.jaxb.resource.JaxbCacheChild;
import org.jboss.resteasy.test.providers.jaxb.resource.JaxbCacheParent;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Jaxb provider
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
public class JaxbCacheTest {

    static ResteasyClient client;
    private static Logger logger = Logger.getLogger(JaxbCacheTest.class.getName());

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(JaxbCacheTest.class.getSimpleName());
        war.addClass(JaxbCacheTest.class);
        // Arquillian in the deployment
        war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
                new ReflectPermission("suppressAccessChecks"),
                new RuntimePermission("accessDeclaredMembers"),
                new PropertyPermission("*", "read")),
                "permissions.xml");

        return TestUtil.finishContainerPrepare(war, null, JaxbCacheParent.class, JaxbCacheChild.class);
    }

    /**
     * @tpTestDetails Gets contextResolver for JAXBContextFinder class and mediatype "APPLICATION_XML_TYPE" or
     *                "APPLICATION_ATOM_XML_TYPE",
     *                then gets calls findCachedContext() twice to get JAXBContext and ensures that the result is the same
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testCache() throws Exception {
        ResteasyProviderFactory factory = ResteasyProviderFactory.getInstance();
        ResteasyContext.pushContext(Providers.class, factory);
        {
            ContextResolver<JAXBContextFinder> resolver = factory.getContextResolver(JAXBContextFinder.class,
                    MediaType.APPLICATION_XML_TYPE);
            JAXBContextFinder finder = resolver.getContext(JaxbCacheChild.class);
            JAXBContext ctx = finder.findCachedContext(JaxbCacheChild.class, MediaType.APPLICATION_XML_TYPE, null);

            JAXBContext ctx2 = finder.findCachedContext(JaxbCacheChild.class, MediaType.APPLICATION_XML_TYPE, null);

            Assertions.assertTrue(ctx == ctx2);
        }

        {
            ContextResolver<JAXBContextFinder> resolver = factory.getContextResolver(JAXBContextFinder.class,
                    MediaType.APPLICATION_ATOM_XML_TYPE);
            JAXBContextFinder finder = resolver.getContext(JaxbCacheChild.class);
            Assertions.assertNotNull(finder);
            JAXBContext ctx = finder.findCachedContext(JaxbCacheChild.class, MediaType.APPLICATION_ATOM_XML_TYPE, null);

            JAXBContext ctx2 = finder.findCachedContext(JaxbCacheChild.class, MediaType.APPLICATION_ATOM_XML_TYPE, null);

            Assertions.assertTrue(ctx == ctx2);
        }
    }

    /**
     * @tpTestDetails Gets contextResolver for JAXBContextFinder class and mediatype "APPLICATION_XML_TYPE" or
     *                "APPLICATION_ATOM_XML_TYPE",
     *                thrn gets calls findCacheContext() twice to get JAXBContext and ensures that the result is the same
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testCache2() throws Exception {
        ResteasyProviderFactory factory = ResteasyProviderFactory.getInstance();
        ResteasyContext.pushContext(Providers.class, factory);
        {
            ContextResolver<JAXBContextFinder> resolver = factory.getContextResolver(JAXBContextFinder.class,
                    MediaType.APPLICATION_XML_TYPE);
            JAXBContextFinder finder = resolver.getContext(JaxbCacheChild.class);
            JAXBContext ctx = finder.findCacheContext(MediaType.APPLICATION_XML_TYPE, null, JaxbCacheChild.class,
                    JaxbCacheParent.class);

            JAXBContext ctx2 = finder.findCacheContext(MediaType.APPLICATION_XML_TYPE, null, JaxbCacheChild.class,
                    JaxbCacheParent.class);

            Assertions.assertTrue(ctx == ctx2);
        }

        {
            ContextResolver<JAXBContextFinder> resolver = factory.getContextResolver(JAXBContextFinder.class,
                    MediaType.APPLICATION_ATOM_XML_TYPE);
            JAXBContextFinder finder = resolver.getContext(JaxbCacheChild.class);
            JAXBContext ctx = finder.findCacheContext(MediaType.APPLICATION_ATOM_XML_TYPE, null, JaxbCacheChild.class,
                    JaxbCacheParent.class);

            JAXBContext ctx2 = finder.findCacheContext(MediaType.APPLICATION_ATOM_XML_TYPE, null, JaxbCacheChild.class,
                    JaxbCacheParent.class);

            Assertions.assertTrue(ctx == ctx2);
        }
    }
}
