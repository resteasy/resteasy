package org.jboss.resteasy.test.resource.basic;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.test.resource.basic.resource.SpecialResourceApiResource;
import org.jboss.resteasy.test.resource.basic.resource.SpecialResourceDeleteResource;
import org.jboss.resteasy.test.resource.basic.resource.SpecialResourceStreamResource;
import org.jboss.resteasy.test.resource.basic.resource.SpecialResourceSubFactory;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.w3c.dom.Document;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

import static org.hamcrest.Matchers.greaterThan;
import static org.jboss.resteasy.util.HttpClient4xUtils.consumeEntity;

/**
 * @tpSubChapter Resources
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEasy issues about special resources
 * @tpSince EAP 7.0.0
 */
@RunWith(Arquillian.class)
@RunAsClient
public class SpecialResourceTest {

    static ResteasyClient client;

    protected static final Logger logger = LogManager.getLogger(SpecialResourceTest.class.getName());

    @Deployment
    public static Archive<?> testReturnValuesDeploy() throws Exception {
        WebArchive war = TestUtil.prepareArchive(SpecialResourceTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, SpecialResourceStreamResource.class,
                SpecialResourceApiResource.class, SpecialResourceDeleteResource.class);
    }

    @BeforeClass
    public static void init() {
        client = new ResteasyClientBuilder().build();
    }

    @AfterClass
    public static void close() {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, SpecialResourceTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Regression test for RESTEASY-631
     * @tpSince EAP 7.0.0
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
     * @tpSince EAP 7.0.0
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
     * @tpSince EAP 7.0.0
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
     * @tpSince EAP 7.0.0
     */
    @Test
    public void test583() throws Exception {
        HttpClient client = HttpClientBuilder.create().build();
        HttpPut method = new HttpPut(generateURL("/api"));
        HttpResponse response = null;
        try {
            method.setEntity(new StringEntity("hello", ContentType.create("vnd.net.juniper.space.target-management.targets+xml;version=1;charset=UTF-8")));
            response = client.execute(method);
            Assert.assertEquals(response.getStatusLine().getStatusCode(), HttpResponseCodes.SC_BAD_REQUEST);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            consumeEntity(response);
        }
    }

    /**
     * @tpTestDetails Regression test for RESTEASY-638
     * @tpSince EAP 7.0.0
     */
    @Test
    public void test638() throws Exception {
        SpecialResourceSubFactory factory = new SpecialResourceSubFactory();
        RegisterBuiltin.register(factory);

        for (int i = 0; i < 10; i++) {
            MediaType type = MediaType.valueOf("text/xml; boundary=" + i);
            Assert.assertThat("Wrong count of possible providers", factory.getMBRMap().getPossible(type, Document.class).size(), greaterThan(1));
        }

        logger.info("cache size: " + factory.getMBRMap().getClassCache().size());
        Assert.assertEquals(1, factory.getMBRMap().getClassCache().size());

    }
}
