package org.jboss.resteasy.test.providers.custom;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.test.providers.custom.resource.SingletonCustomProviderApplication;
import org.jboss.resteasy.test.providers.custom.resource.SingletonCustomProviderObject;
import org.jboss.resteasy.test.providers.custom.resource.SingletonCustomProviderResource;
import org.jboss.resteasy.utils.PortProviderUtil;
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
 * @tpTestCaseDetails Test for anonymous classes as resource added to REST singletons
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class SingletonCustomProviderTest {
    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = ShrinkWrap.create(WebArchive.class, SingletonCustomProviderTest.class.getSimpleName() + ".war");
        war.addClasses(SingletonCustomProviderApplication.class, SingletonCustomProviderObject.class,
                SingletonCustomProviderResource.class);
        return war;
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, SingletonCustomProviderTest.class.getSimpleName());
    }

    @BeforeEach
    public void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterEach
    public void after() throws Exception {
        client.close();
    }

    /**
     * @tpTestDetails Check post request
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMessageReaderThrowingWebApplicationException() throws Exception {
        Response response = client.target(generateURL("/test")).request()
                .post(Entity.entity("foo", "application/octet-stream"));
        Assertions.assertEquals(999, response.getStatus(), "Wrong response status");
        response.close();
    }

    /**
     * @tpTestDetails Check get request
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMessageWriterThrowingWebApplicationException() throws Exception {
        Response response = client.target(generateURL("/test")).request().get();
        Assertions.assertEquals(999, response.getStatus(), "Wrong response status");
        response.close();
    }
}
