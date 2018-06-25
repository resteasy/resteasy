package org.jboss.resteasy.test.providers.jackson;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.category.ExpectedFailingOnWildFly13;
import org.jboss.resteasy.client.ClientRequest; //@cs-: clientrequest (Old client test)
import org.jboss.resteasy.client.ClientResponse; //@cs-: clientresponse (Old client test)
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.providers.jackson.resource.ProxyWithGenericReturnTypeJacksonAbstractParent;
import org.jboss.resteasy.test.providers.jackson.resource.ProxyWithGenericReturnTypeJacksonInvocationHandler;
import org.jboss.resteasy.test.providers.jackson.resource.ProxyWithGenericReturnTypeJacksonResource;
import org.jboss.resteasy.test.providers.jackson.resource.ProxyWithGenericReturnTypeJacksonSubResourceIntf;
import org.jboss.resteasy.test.providers.jackson.resource.ProxyWithGenericReturnTypeJacksonSubResourceSubIntf;
import org.jboss.resteasy.test.providers.jackson.resource.ProxyWithGenericReturnTypeJacksonType1;
import org.jboss.resteasy.test.providers.jackson.resource.ProxyWithGenericReturnTypeJacksonType2;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import javax.ws.rs.core.Response;

/**
 * @tpSubChapter Jackson provider
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test jackson1 provider with proxy with generic return type
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
@Category({ExpectedFailingOnWildFly13.class})
public class ProxyWithGenericReturnTypeJacksonTest {

    protected static final Logger logger = Logger.getLogger(ProxyWithGenericReturnTypeJacksonTest.class.getName());

    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ProxyWithGenericReturnTypeJacksonTest.class.getSimpleName());
        war.addClasses(ProxyWithGenericReturnTypeJacksonAbstractParent.class, ProxyWithGenericReturnTypeJacksonInvocationHandler.class,
                ProxyWithGenericReturnTypeJacksonSubResourceIntf.class, ProxyWithGenericReturnTypeJacksonSubResourceSubIntf.class,
                ProxyWithGenericReturnTypeJacksonType1.class, ProxyWithGenericReturnTypeJacksonType2.class);
        war.addAsManifestResource("jboss-deployment-structure-jackson-v1.xml", "jboss-deployment-structure.xml");
        return TestUtil.finishContainerPrepare(war, null, ProxyWithGenericReturnTypeJacksonResource.class);
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
        return PortProviderUtil.generateURL(path, ProxyWithGenericReturnTypeJacksonTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Test new client
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testNewClient() throws Exception {
        Response response = client.target(generateURL("/test/one/")).request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertTrue("Type property is missing.", response.readEntity(String.class).contains("type"));
        response.close();

        response = client.target(generateURL("/test/list/")).request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertTrue("Type property is missing.", response.readEntity(String.class).contains("type"));
        response.close();
    }

    /**
     * @tpTestDetails Test old client
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testOldClient() throws Exception {
        ClientRequest request = new ClientRequest(generateURL("/test/one/"));
        ClientResponse<String> response = request.get(String.class);
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertTrue("Type property is missing.", response.getEntity(String.class).contains("type"));
        response.releaseConnection();

        request = new ClientRequest(generateURL("/test/list/"));
        response = request.get(String.class);
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertTrue("Type property is missing.", response.getEntity(String.class).contains("type"));
        response.releaseConnection();
    }
}
