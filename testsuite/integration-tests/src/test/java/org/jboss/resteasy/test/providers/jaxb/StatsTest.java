package org.jboss.resteasy.test.providers.jaxb;

import java.util.ArrayList;
import java.util.List;

import jakarta.ws.rs.client.ClientBuilder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.plugins.stats.DeleteResourceMethod;
import org.jboss.resteasy.plugins.stats.GetResourceMethod;
import org.jboss.resteasy.plugins.stats.HeadResourceMethod;
import org.jboss.resteasy.plugins.stats.OptionsResourceMethod;
import org.jboss.resteasy.plugins.stats.PostResourceMethod;
import org.jboss.resteasy.plugins.stats.PutResourceMethod;
import org.jboss.resteasy.plugins.stats.RegistryData;
import org.jboss.resteasy.plugins.stats.RegistryEntry;
import org.jboss.resteasy.plugins.stats.RegistryStatsResource;
import org.jboss.resteasy.plugins.stats.ResourceMethodEntry;
import org.jboss.resteasy.plugins.stats.SubresourceLocator;
import org.jboss.resteasy.plugins.stats.TraceResourceMethod;
import org.jboss.resteasy.test.providers.jaxb.resource.StatsProxy;
import org.jboss.resteasy.test.providers.jaxb.resource.StatsResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Jaxb provider
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class StatsTest {

    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(StatsTest.class.getSimpleName());
        war.addClass(StatsTest.class);
        return TestUtil.finishContainerPrepare(war, null, StatsResource.class, RegistryStatsResource.class,
                ResourceMethodEntry.class, GetResourceMethod.class, PutResourceMethod.class, DeleteResourceMethod.class,
                PostResourceMethod.class, OptionsResourceMethod.class, HeadResourceMethod.class, TraceResourceMethod.class,
                RegistryData.class, RegistryEntry.class, SubresourceLocator.class);
    }

    @BeforeEach
    public void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterEach
    public void after() throws Exception {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, StatsTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Tests resteasy jaxb utility RegistryStatsResource, it is getting information about resources available
     *                to the application
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testRegistryStats() throws Exception {
        StatsProxy stats = client.target(generateURL("/")).proxy(StatsProxy.class);

        RegistryData data = stats.get();
        Assertions.assertEquals(4, data.getEntries().size(),
                "The number of resources doesn't match");
        boolean found = false;
        for (RegistryEntry entry : data.getEntries()) {
            if (entry.getUriTemplate().equals("/entry/{foo:.*}")) {
                Assertions.assertEquals(2, entry.getMethods().size(),
                        "Some method for resource \"" + entry.getUriTemplate() + "\" is missing ");
                List<Class> prepareRequiredTypes = prepareRequiredTypes(PostResourceMethod.class, PutResourceMethod.class);
                Assertions.assertTrue(testMethodTypes(entry.getMethods().get(0), prepareRequiredTypes),
                        "Unexpected method type");
                Assertions.assertTrue(testMethodTypes(entry.getMethods().get(1), prepareRequiredTypes),
                        "Unexpected method type");
                found = true;
                break;
            }
        }
        Assertions.assertTrue(found, "Resource not found");
        found = false;
        for (RegistryEntry entry : data.getEntries()) {
            if (entry.getUriTemplate().equals("/resource")) {
                Assertions.assertEquals(2, entry.getMethods().size(),
                        "Some method for resource \"" + entry.getUriTemplate() + "\" is missing ");
                List<Class> prepareRequiredTypes = prepareRequiredTypes(HeadResourceMethod.class, DeleteResourceMethod.class);
                Assertions.assertTrue(testMethodTypes(entry.getMethods().get(0), prepareRequiredTypes),
                        "Unexpected method type");
                Assertions.assertTrue(testMethodTypes(entry.getMethods().get(1), prepareRequiredTypes),
                        "Unexpected method type");
                found = true;
                break;
            }
        }
        Assertions.assertTrue(found, "Resource not found");
        found = false;
        for (RegistryEntry entry : data.getEntries()) {
            if (entry.getUriTemplate().equals("/locator")) {
                Assertions.assertNotNull(entry.getLocator());
                found = true;
                break;
            }
        }
        Assertions.assertTrue(found, "Resource not found");
        found = false;
        for (RegistryEntry entry : data.getEntries()) {
            if (entry.getUriTemplate().equals("/resteasy/registry")) {
                Assertions.assertEquals(1, entry.getMethods().size(),
                        "Some method for resource \"" + entry.getUriTemplate() + "\" is missing ");
                Assertions.assertTrue(entry.getMethods().get(0) instanceof GetResourceMethod,
                        "Unexpected method type");
                found = true;
                break;
            }
        }
        Assertions.assertTrue(found, "Resource not found");

    }

    private boolean testMethodTypes(ResourceMethodEntry entry, List<Class> types) {
        if (types.contains(entry.getClass())) {
            types.remove(entry.getClass());
            return true;
        } else {
            return false;
        }
    }

    private List<Class> prepareRequiredTypes(Class... types) {
        ArrayList<Class> list = new ArrayList<Class>();
        for (Class type : types) {
            list.add(type);
        }
        return list;
    }
}
