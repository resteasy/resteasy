package org.jboss.resteasy.test.core.servlet;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.test.core.servlet.resource.ServletMappingProxy;
import org.jboss.resteasy.test.core.basic.resource.MyFilter;
import org.jboss.resteasy.test.core.servlet.resource.ServletMappingResource;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

/**
 * @tpSubChapter Configuration
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for servlet mapping. This settings is in web.xml
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ServletMappingTest {
    public static final String WRONG_RESPONSE_ERROR_MSG = "Wrong content of response";

    private static ResteasyClient client;

    @Deployment
    public static Archive<?> deploySimpleResource() {
        WebArchive war = TestUtil.prepareArchive(ServletMappingTest.class.getSimpleName());
        war.addAsWebInfResource(ServletMappingTest.class.getPackage(), "ServletMappingWeb.xml", "web.xml");
        war.addAsWebInfResource(ServletMappingTest.class.getPackage(), "ServletMappingJbossWeb.xml", "jboss-web.xml");
        war.addClass(MyFilter.class);
        return TestUtil.finishContainerPrepare(war, null, ServletMappingResource.class, ServletMappingProxy.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, "");
    }

    @Before
    public void setup() {
        client = new ResteasyClientBuilder().build();
    }

    @After
    public void after() throws Exception {
        client.close();
    }

    /**
     * @tpTestDetails Test for new resteasy client without proxy
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testNoDefaultsResourceNewRESTEasyClient() throws Exception {
        WebTarget target = client.target(generateURL("/resteasy/rest/basic"));
        Response response = target.request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals(WRONG_RESPONSE_ERROR_MSG, "basic", response.readEntity(String.class));
        response.close();
    }

    /**
     * @tpTestDetails Test for apache client
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testNoDefaultsResourceApacheClient() throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(generateURL("/resteasy/rest/basic"));
        CloseableHttpResponse response1 = httpclient.execute(httpGet);

        try {
            Assert.assertEquals(HttpResponseCodes.SC_OK, response1.getStatusLine().getStatusCode());
            Assert.assertEquals(WRONG_RESPONSE_ERROR_MSG, "basic", TestUtil.readString(response1.getEntity().getContent()));
        } finally {
            response1.close();
        }
    }

    /**
     * @tpTestDetails Test for new resteasy client with proxy
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testFormParamNewRESTEasyClient() {
        ResteasyWebTarget target = client.target(generateURL("/resteasy/rest"));
        ServletMappingProxy client = target.proxyBuilder(ServletMappingProxy.class).build();
        final String result = client.postForm("value");
        Assert.assertEquals(WRONG_RESPONSE_ERROR_MSG, result, "value");
    }
}
