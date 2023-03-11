package org.jboss.resteasy.test.client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeSet;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ProxyBuilder;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.client.resource.ParameterListInterface;
import org.jboss.resteasy.test.client.resource.ParameterListResource;
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
 * @tpSubChapter Resteasy-client
 * @tpChapter Client tests
 * @tpSince RESTEasy 3.0.16
 * @tpTestCaseDetails Regression for RESTEASY-756
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ParameterListTest extends ClientTestBase {

    protected static final Logger logger = Logger.getLogger(ParameterListTest.class.getName());

    private static final String ERROR_MESSAGE = "Wrong parameters in response received";
    private static Client restClient;

    @Before
    public void init() {
        restClient = ClientBuilder.newClient();
    }

    @After
    public void after() throws Exception {
        restClient.close();
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ParameterListTest.class.getSimpleName());
        war.addClass(ParameterListInterface.class);
        return TestUtil.finishContainerPrepare(war, null, ParameterListResource.class);
    }

    private String generateBaseUrl() {
        return PortProviderUtil.generateBaseUrl(ParameterListTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails New client: set matrix param by URL and by matrixParam function
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMatrixNewClient() throws Exception {
        Response response = restClient.target(generateURL("/matrix;m1=a/list;m1=b;p2=c")).matrixParam("m1", "d").request()
                .get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals(ERROR_MESSAGE, "a:b:d:", response.readEntity(String.class));
    }

    /**
     * @tpTestDetails New client: set query param by URL and by queryParam function
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testQueryNewClient() throws Exception {
        Response response = restClient.target(generateURL("/query/list?q1=a&q2=b&q1=c")).queryParam("q1", "d").request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals(ERROR_MESSAGE, "a:c:d:", response.readEntity(String.class));
    }

    /**
     * @tpTestDetails New client: check settings of matrix list by proxy
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMatrixProxyListNewClient() throws Exception {
        final ParameterListInterface client = ProxyBuilder.builder(ParameterListInterface.class,
                restClient.target(generateBaseUrl())).build();
        ArrayList<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        list.add("c");
        Response response = client.matrixList(list);
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals(ERROR_MESSAGE, "a:b:c:", response.readEntity(String.class));
        response.close();
    }

    /**
     * @tpTestDetails New client: check settings of matrix set by proxy
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMatrixProxySetNewClient() throws Exception {
        final ParameterListInterface client = ProxyBuilder.builder(ParameterListInterface.class,
                restClient.target(generateBaseUrl())).build();
        HashSet<String> set = new HashSet<>();
        set.add("a");
        set.add("b");
        set.add("c");
        Response response = client.matrixSet(set);
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals(ERROR_MESSAGE, "a:b:c:", response.readEntity(String.class));
    }

    /**
     * @tpTestDetails New client: check settings of matrix sorted set by proxy
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMatrixProxySortedSetNewClient() throws Exception {
        final ParameterListInterface client = ProxyBuilder.builder(ParameterListInterface.class,
                restClient.target(generateBaseUrl())).build();
        TreeSet<String> set = new TreeSet<String>();
        set.add("a");
        set.add("b");
        set.add("c");
        Response response = client.matrixSortedSet(set);
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals(ERROR_MESSAGE, "a:b:c:", response.readEntity(String.class));
    }

    /**
     * @tpTestDetails New client: check settings of matrix list and other parameter by proxy
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMatrixWithEntityProxyNewClient() throws Exception {
        final ParameterListInterface client = ProxyBuilder.builder(ParameterListInterface.class,
                restClient.target(generateBaseUrl())).build();
        ArrayList<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        list.add("c");
        Response response = client.matrixWithEntity(list, "entity");
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals(ERROR_MESSAGE, "entity:a:b:c:", response.readEntity(String.class));
    }

    /**
     * @tpTestDetails New client: check settings of query list by proxy
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testQueryProxyListNewClient() throws Exception {
        final ParameterListInterface client = ProxyBuilder.builder(ParameterListInterface.class,
                restClient.target(generateBaseUrl())).build();
        ArrayList<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        list.add("c");
        Response response = client.queryList(list);
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals(ERROR_MESSAGE, "a:b:c:", response.readEntity(String.class));
    }

    /**
     * @tpTestDetails New client: check settings of query set by proxy
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testQueryProxySetNewClient() throws Exception {
        final ParameterListInterface client = ProxyBuilder.builder(ParameterListInterface.class,
                restClient.target(generateBaseUrl())).build();
        HashSet<String> set = new HashSet<>();
        set.add("a");
        set.add("b");
        set.add("c");
        Response response = client.querySet(set);
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals(ERROR_MESSAGE, "a:b:c:", response.readEntity(String.class));
    }

    /**
     * @tpTestDetails New client: check settings of query sorted set by proxy
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testQueryProxySortedSetNewClient() throws Exception {
        final ParameterListInterface client = ProxyBuilder.builder(ParameterListInterface.class,
                restClient.target(generateBaseUrl())).build();
        TreeSet<String> set = new TreeSet<String>();
        set.add("a");
        set.add("b");
        set.add("c");
        Response response = client.querySortedSet(set);
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals(ERROR_MESSAGE, "a:b:c:", response.readEntity(String.class));
    }

    /**
     * @tpTestDetails New client: check settings of query list with other parameter by proxy
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testQueryWithEntityProxyNewClient() throws Exception {
        final ParameterListInterface client = ProxyBuilder.builder(ParameterListInterface.class,
                restClient.target(generateBaseUrl())).build();
        ArrayList<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        list.add("c");
        Response response = client.queryWithEntity(list, "entity");
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals(ERROR_MESSAGE, "entity:a:b:c:", response.readEntity(String.class));
    }

    /**
     * @tpTestDetails New client: check settings of query list, matrix list and other parameter by proxy
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMatrixQueryWithEntityProxyNewClient() throws Exception {
        final ParameterListInterface client = ProxyBuilder.builder(ParameterListInterface.class,
                restClient.target(generateBaseUrl())).build();
        ArrayList<String> matrixParams = new ArrayList<>();
        matrixParams.add("a");
        matrixParams.add("b");
        matrixParams.add("c");
        ArrayList<String> queryParams = new ArrayList<>();
        queryParams.add("x");
        queryParams.add("y");
        queryParams.add("z");
        Response response = client.matrixQueryWithEntity(matrixParams, queryParams, "entity");
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals(ERROR_MESSAGE, "entity:a:b:c:x:y:z:", response.readEntity(String.class));
    }
}
