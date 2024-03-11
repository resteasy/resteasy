package org.jboss.resteasy.test.core.basic;

import jakarta.ws.rs.client.ClientBuilder;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.core.basic.resource.AppConfigApplication;
import org.jboss.resteasy.test.core.basic.resource.AppConfigResources;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Configuration
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for resource and provider defined in one class together.
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class AppConfigTest {

    private static ResteasyClient client;

    @Deployment
    public static Archive<?> deploySimpleResource() {
        WebArchive war = ShrinkWrap.create(WebArchive.class, AppConfigTest.class.getSimpleName() + ".war");
        war.addClass(AppConfigResources.class);
        war.addClass(AppConfigApplication.class);
        war.addAsWebInfResource(AppConfigTest.class.getPackage(), "AppConfigWeb.xml", "web.xml");
        return war;
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, AppConfigTest.class.getSimpleName());
    }

    @BeforeEach
    public void setup() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterEach
    public void after() throws Exception {
        client.close();
    }

    /**
     * @tpTestDetails Test for apache client
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void apacheClient() throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(generateURL("/my"));
        CloseableHttpResponse response1 = httpclient.execute(httpGet);

        try {
            Assertions.assertEquals(HttpResponseCodes.SC_OK, response1.getStatusLine().getStatusCode());
            Assertions.assertEquals("\"hello\"", TestUtil.readString(response1.getEntity().getContent()));
        } finally {
            response1.close();
        }
    }
}
