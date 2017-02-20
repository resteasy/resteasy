package org.jboss.resteasy.test.spring.deployment;

import javax.json.JsonArray;

import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.spring.deployment.resource.*;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtilSpring;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Test that springframework MVC works in conjuction with jaxrs Application subclass.
 * It's all about having the proper configuration in the web.xml.
 * User: rsearls
 * Date: 2/20/17
 */
@RunWith(Arquillian.class)
@RunAsClient
public class JaxrsWithSpringMVCTest {

    static ResteasyClient client;

    @Deployment
    private static Archive<?> deploy() {
        WebArchive archive = ShrinkWrap.create(WebArchive.class, JaxrsWithSpringMVCTest.class.getSimpleName() + ".war")
            .addAsWebInfResource(JaxrsWithSpringMVCTest.class.getPackage(), "jaxrsWithSpringMVC/web.xml", "web.xml");
        archive.addAsWebInfResource(JaxrsWithSpringMVCTest.class.getPackage(),
            "jaxrsWithSpringMVC/spring-servlet.xml", "spring-servlet.xml");
        archive.addClass(GreetingController.class);
        archive.addClass(Greeting.class);
        archive.addClass(NumbersResource.class);
        archive.addClass(JaxrsApplication.class);
        TestUtilSpring.addSpringLibraries(archive);
        return archive;
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
        return PortProviderUtil.generateURL(path, JaxrsWithSpringMVCTest.class.getSimpleName());
    }

    /**
     */
    @Test
    public void testAllEndpoints() throws Exception {

        {
            WebTarget target = client.target(generateURL("/greeting"));
            Response response = target.request().get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            String str = response.readEntity(String.class);
            Assert.assertEquals("Unexpected response content from the server", "\"World\"", str);
        }

        {
            WebTarget target = client.target(generateURL("/numbers"));
            Response response = target.request().get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            JsonArray ja = response.readEntity(JsonArray.class);
            Assert.assertEquals("Unexpected response content from the server", 10, ja.size());
        }

        {
            WebTarget target = client.target(generateURL("/resources/numbers"));
            Response response = target.request().get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            JsonArray ja = response.readEntity(JsonArray.class);
            Assert.assertEquals("Unexpected response content from the server", 10, ja.size());
        }

    }

}
