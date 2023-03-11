package org.jboss.resteasy.test.client;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.FeatureContext;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.RuntimeDelegate;

import org.jboss.resteasy.utils.TestUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Unit tests
 * @tpSince RESTEasy 3.0.16
 *
 */
public class ClientBuilderTest {

    /**
     * @tpTestDetails Create string entity with unparsable media type
     * @tpPassCrit IllegalArgumentException is raised
     * @tpSince RESTEasy 3.0.16
     */
    @Test(expected = IllegalArgumentException.class)
    public void entityStringThrowsExceptionWhenUnparsableTest() throws Exception {
        Entity.entity("entity", "\\//\\");
        Assert.fail();
    }

    /**
     * @tpTestDetails Create client, set up custom property for it, get client configuration
     *                and create new client using that configuration
     * @tpPassCrit Client is created
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testBuilder() throws Exception {
        String property = "prop";
        Client client = ClientBuilder.newClient();
        client.property(property, property);
        Configuration config = client.getConfiguration();
        client = ClientBuilder.newClient(config);

    }

    /**
     * @tpTestDetails Create client with custom property, check that property is set and remove the property
     *                from client configuration
     * @tpPassCrit Property is added and removed from client configuration
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void addAndRemovePropertyTest() throws Exception {
        String property = "prop";
        Client client = ClientBuilder.newClient();
        client.property(property, property);
        Object p = client.getConfiguration().getProperty(property);
        Assert.assertEquals("prop", (String) p);
        try {
            client.property(property, null);
        } catch (NullPointerException e) {
            Assert.fail(TestUtil.getErrorMessageForKnownIssue("JBEAP-324", "Couldn't remove property"));
        }
        p = client.getConfiguration().getProperty(property);
        Assert.assertEquals(null, p);
    }

    /**
     * @tpTestDetails Invoke target method on the closed client
     * @tpPassCrit IllegalStateException is raised
     * @tpSince RESTEasy 3.0.16
     */
    @Test(expected = IllegalStateException.class)
    public void closeClientSendRequestTest() throws Exception {
        Client client = ClientBuilder.newClient();
        client.close();
        client.target(generateURL("/"));
    }

    /**
     * @tpTestDetails Create Webtarget instance, close the client and execute get request on the original
     *                Webtarget instance
     * @tpPassCrit IllegalStateException is raised
     * @tpSince RESTEasy 3.0.16
     */
    @Test(expected = IllegalStateException.class)
    public void closeClientWebTargetTest() throws Exception {
        Client client = ClientBuilder.newClient();
        WebTarget base = client.target(generateURL("/") + "/test");
        client.close();
        Response response = base.request().get();
    }

    /**
     * @tpTestDetails Create link instance with jaxrs spec apis to check there is no NPE thrown
     * @tpPassCrit Link object is successfully created
     * @tpSince RESTEasy 3.5
     */
    @Test
    public void testLinkBuilder() throws Exception {
        Link link = RuntimeDelegate.getInstance().createLinkBuilder()
                .baseUri("http://jboss.org/resteasy").rel("relation relation2").title("titleX")
                .param("param1", "value1").param("param2", "value2")
                .type(MediaType.APPLICATION_OCTET_STREAM).build();
        Assert.assertNotNull("Build link failed", link);
    }

    @Test
    public void testRegisterContextResolverClass() {
        ClientBuilder.newBuilder()
                .register(new CustomContextResolver())
                .build();
    }

    @Test
    public void testRegisterContextResolverAnonymousClass() {
        ClientBuilder.newBuilder()
                .register(new ContextResolver<MyObject>() {

                    @Override
                    public MyObject getContext(Class<?> type) {
                        return null;
                    }

                })
                .build();
    }

    @Test(expected = RuntimeException.class)
    public void testRegisterContextResolverLambda() {
        ClientBuilder.newBuilder()
                .register((ContextResolver<MyObject>) type -> null)
                .build();
    }

    public static class MyObject {

    }

    public static class CustomContextResolver implements ContextResolver<MyObject> {

        @Override
        public MyObject getContext(Class<?> type) {
            return null;
        }

    }

    public static class FeatureReturningFalse implements Feature {
        @Override
        public boolean configure(FeatureContext context) {
            // false returning feature is not to be registered
            return false;
        }
    }
}
