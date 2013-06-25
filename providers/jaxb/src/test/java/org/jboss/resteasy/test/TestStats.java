package org.jboss.resteasy.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.jboss.resteasy.test.TestPortProvider.*;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import junit.framework.Assert;

import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.plugins.stats.DeleteResourceMethod;
import org.jboss.resteasy.plugins.stats.GetResourceMethod;
import org.jboss.resteasy.plugins.stats.HeadResourceMethod;
import org.jboss.resteasy.plugins.stats.PostResourceMethod;
import org.jboss.resteasy.plugins.stats.PutResourceMethod;
import org.jboss.resteasy.plugins.stats.RegistryData;
import org.jboss.resteasy.plugins.stats.RegistryEntry;
import org.jboss.resteasy.plugins.stats.RegistryStatsResource;
import org.jboss.resteasy.plugins.stats.ResourceMethodEntry;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class TestStats extends BaseResourceTest {
    @Path("/")
    public static class MyResource {
        @Path("locator")
        public Object getLocator() {
            return null;
        }

        @Path("entry/{foo:.*}")
        @PUT
        @Produces("text/xml")
        @Consumes("application/json")
        public void put() {

        }

        @Path("entry/{foo:.*}")
        @POST
        @Produces("text/xml")
        @Consumes("application/json")
        public void post() {

        }

        @DELETE
        @Path("resource")
        public void delete() {
        }

        @HEAD
        @Path("resource")
        public void head() {
        }

    }

    @Path("/resteasy/registry")
    public interface RegistryStats {
        @GET
        @Produces("application/xml")
        public RegistryData get();
    }

    @Before
    public void setUp() throws Exception {
        dispatcher.getRegistry().addPerRequestResource(MyResource.class);
        dispatcher.getRegistry().addPerRequestResource(RegistryStatsResource.class);
    }

    @Test
    public void testRegistryStats() throws Exception {
        RegistryStats stats = ProxyFactory.create(RegistryStats.class, generateBaseUrl());

        RegistryData data = stats.get();
        Assert.assertEquals(4, data.getEntries().size());
        boolean found = false;
        for (RegistryEntry entry : data.getEntries()) {
            if (entry.getUriTemplate().equals("/entry/{foo:.*}")) {
                Assert.assertEquals(2, entry.getMethods().size());
                List<Class> prepareRequiredTypes = prepareRequiredTypes(PostResourceMethod.class, PutResourceMethod.class);
                Assert.assertTrue(testMethodTypes(entry.getMethods().get(0), prepareRequiredTypes));
                Assert.assertTrue(testMethodTypes(entry.getMethods().get(1), prepareRequiredTypes));
                found = true;
                break;
            }
        }
        Assert.assertTrue(found);
        found = false;
        for (RegistryEntry entry : data.getEntries()) {
            if (entry.getUriTemplate().equals("/resource")) {
                Assert.assertEquals(2, entry.getMethods().size());
                List<Class> prepareRequiredTypes = prepareRequiredTypes(HeadResourceMethod.class, DeleteResourceMethod.class);
                Assert.assertTrue(testMethodTypes(entry.getMethods().get(0), prepareRequiredTypes));
                Assert.assertTrue(testMethodTypes(entry.getMethods().get(1), prepareRequiredTypes));
                found = true;
                break;
            }
        }
        Assert.assertTrue(found);
        found = false;
        for (RegistryEntry entry : data.getEntries()) {
            if (entry.getUriTemplate().equals("/locator")) {
                Assert.assertNotNull(entry.getLocator());
                found = true;
                break;
            }
        }
        Assert.assertTrue(found);
        found = false;
        for (RegistryEntry entry : data.getEntries()) {
            if (entry.getUriTemplate().equals("/resteasy/registry")) {
                Assert.assertEquals(1, entry.getMethods().size());
                Assert.assertTrue(entry.getMethods().get(0) instanceof GetResourceMethod);
                found = true;
                break;
            }
        }
        Assert.assertTrue(found);

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
        return new ArrayList(Arrays.asList(types));
    }
}
