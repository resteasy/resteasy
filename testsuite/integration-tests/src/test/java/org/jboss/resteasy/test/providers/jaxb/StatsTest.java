package org.jboss.resteasy.test.providers.jaxb;

import java.util.ArrayList;
import java.util.List;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.plugins.stats.RegistryStatsResource;
import org.jboss.resteasy.plugins.stats.RegistryData;
import org.jboss.resteasy.plugins.stats.RegistryEntry;
import org.jboss.resteasy.plugins.stats.PostResourceMethod;
import org.jboss.resteasy.plugins.stats.PutResourceMethod;
import org.jboss.resteasy.plugins.stats.HeadResourceMethod;
import org.jboss.resteasy.plugins.stats.OptionsResourceMethod;
import org.jboss.resteasy.plugins.stats.DeleteResourceMethod;
import org.jboss.resteasy.plugins.stats.ResourceMethodEntry;
import org.jboss.resteasy.plugins.stats.SubresourceLocator;
import org.jboss.resteasy.plugins.stats.TraceResourceMethod;
import org.jboss.resteasy.plugins.stats.GetResourceMethod;
import org.jboss.resteasy.test.providers.jaxb.resource.StatsProxy;
import org.jboss.resteasy.test.providers.jaxb.resource.StatsResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter Jaxb provider
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
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

    @Before
    public void init() {
        client = new ResteasyClientBuilder().build();
    }

    @After
    public void after() throws Exception {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, StatsTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Tests resteasy jaxb utility RegistryStatsResource, it is getting information about resources available
     * to the application
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testRegistryStats() throws Exception {
        StatsProxy stats = client.target(generateURL("/")).proxy(StatsProxy.class);

        RegistryData data = stats.get();
        Assert.assertEquals("The number of resources doesn't match", 4, data.getEntries().size());
        boolean found = false;
        for (RegistryEntry entry : data.getEntries()) {
            if (entry.getUriTemplate().equals("/entry/{foo:.*}")) {
                Assert.assertEquals("Some method for resource \"" + entry.getUriTemplate() + "\" is missing ", 2,
                        entry.getMethods().size());
                List<Class> prepareRequiredTypes = prepareRequiredTypes(PostResourceMethod.class, PutResourceMethod.class);
                Assert.assertTrue("Unexpected method type", testMethodTypes(entry.getMethods().get(0), prepareRequiredTypes));
                Assert.assertTrue("Unexpected method type", testMethodTypes(entry.getMethods().get(1), prepareRequiredTypes));
                found = true;
                break;
            }
        }
        Assert.assertTrue("Resource not found", found);
        found = false;
        for (RegistryEntry entry : data.getEntries()) {
            if (entry.getUriTemplate().equals("/resource")) {
                Assert.assertEquals("Some method for resource \"" + entry.getUriTemplate() + "\" is missing ", 2,
                        entry.getMethods().size());
                List<Class> prepareRequiredTypes = prepareRequiredTypes(HeadResourceMethod.class, DeleteResourceMethod.class);
                Assert.assertTrue("Unexpected method type", testMethodTypes(entry.getMethods().get(0), prepareRequiredTypes));
                Assert.assertTrue("Unexpected method type", testMethodTypes(entry.getMethods().get(1), prepareRequiredTypes));
                found = true;
                break;
            }
        }
        Assert.assertTrue("Resource not found", found);
        found = false;
        for (RegistryEntry entry : data.getEntries()) {
            if (entry.getUriTemplate().equals("/locator")) {
                Assert.assertNotNull(entry.getLocator());
                found = true;
                break;
            }
        }
        Assert.assertTrue("Resource not found", found);
        found = false;
        for (RegistryEntry entry : data.getEntries()) {
            if (entry.getUriTemplate().equals("/resteasy/registry")) {
                Assert.assertEquals("Some method for resource \"" + entry.getUriTemplate() + "\" is missing ", 1,
                        entry.getMethods().size());
                Assert.assertTrue("Unexpected method type", entry.getMethods().get(0) instanceof GetResourceMethod);
                found = true;
                break;
            }
        }
        Assert.assertTrue("Resource not found", found);

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
