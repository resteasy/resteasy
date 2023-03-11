package org.jboss.resteasy.test.resource.basic;

import java.io.IOException;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.resource.basic.resource.SpecialResourceApiResource;
import org.jboss.resteasy.test.resource.basic.resource.SpecialResourceDeleteResource;
import org.jboss.resteasy.test.resource.basic.resource.SpecialResourceStreamResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter Resources
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEasy issues about special resources
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class SpecialResourceTest {

    static ResteasyClient client;

    @Deployment
    public static Archive<?> testReturnValuesDeploy() throws Exception {
        WebArchive war = TestUtil.prepareArchive(SpecialResourceTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, SpecialResourceStreamResource.class,
                SpecialResourceApiResource.class, SpecialResourceDeleteResource.class);
    }

    @BeforeClass
    public static void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterClass
    public static void close() {
        client.close();
        client = null;
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, SpecialResourceTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Regression test for RESTEASY-631
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void test631() throws Exception {
        WebTarget base = client.target(generateURL("/delete"));
        Response response = base.request().method("DELETE", Entity.entity("hello", "text/plain"));
        Assert.assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus());
        response.close();
    }

    /**
     * @tpTestDetails Regression test for RESTEASY-534
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void test534() throws Exception {
        WebTarget base = client.target(generateURL("/inputstream/test/json"));
        Response response = base.request().post(Entity.entity("hello world".getBytes(), MediaType.APPLICATION_OCTET_STREAM));
        Assert.assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus());
        response.close();
    }

    /**
     * @tpTestDetails Regression test for RESTEASY-624
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void test624() throws Exception {
        WebTarget base = client.target(generateURL("/ApI/FuNc"));
        Response response = base.request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        response.close();

    }

    /**
     * @tpTestDetails Regression test for RESTEASY-583
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void test583() throws Exception {
        HttpClient client = HttpClientBuilder.create().build();
        HttpPut method = new HttpPut(generateURL("/api"));
        HttpResponse response = null;
        try {
            method.setEntity(
                    new StringEntity("hello", ContentType.create("vnd.net.juniper.space.target-management.targets+xml")));
            response = client.execute(method);
            Assert.assertEquals(response.getStatusLine().getStatusCode(), HttpResponseCodes.SC_BAD_REQUEST);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (response != null) {
                    EntityUtils.consume(response.getEntity());
                }
            } catch (IOException e) {
                LogMessages.LOGGER.unableToCloseEntityStream(e);
            }
        }
    }

}
