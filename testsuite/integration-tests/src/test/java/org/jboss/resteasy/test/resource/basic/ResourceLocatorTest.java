package org.jboss.resteasy.test.resource.basic;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation.Builder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.test.resource.basic.resource.ResourceLocatorAbstractAnnotationFreeResouce;
import org.jboss.resteasy.test.resource.basic.resource.ResourceLocatorAnnotationFreeSubResource;
import org.jboss.resteasy.test.resource.basic.resource.ResourceLocatorBaseResource;
import org.jboss.resteasy.test.resource.basic.resource.ResourceLocatorCollectionResource;
import org.jboss.resteasy.test.resource.basic.resource.ResourceLocatorDirectory;
import org.jboss.resteasy.test.resource.basic.resource.ResourceLocatorQueueReceiver;
import org.jboss.resteasy.test.resource.basic.resource.ResourceLocatorReceiver;
import org.jboss.resteasy.test.resource.basic.resource.ResourceLocatorRootInterface;
import org.jboss.resteasy.test.resource.basic.resource.ResourceLocatorSubInterface;
import org.jboss.resteasy.test.resource.basic.resource.ResourceLocatorSubresource;
import org.jboss.resteasy.test.resource.basic.resource.ResourceLocatorSubresource2;
import org.jboss.resteasy.test.resource.basic.resource.ResourceLocatorSubresource3;
import org.jboss.resteasy.test.resource.basic.resource.ResourceLocatorSubresource3Interface;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Resource
 * @tpChapter Integration tests
 * @tpTestCaseDetails Tests path encoding
 * @tpSince RESTEasy 3.0.20
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ResourceLocatorTest {
    static Client client;

    @BeforeAll
    public static void setup() {
        client = ClientBuilder.newClient();
    }

    @AfterAll
    public static void close() {
        client.close();
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ResourceLocatorTest.class.getSimpleName());
        war.addClass(ResourceLocatorQueueReceiver.class)
                .addClass(ResourceLocatorReceiver.class)
                .addClass(ResourceLocatorRootInterface.class)
                .addClass(ResourceLocatorSubInterface.class)
                .addClass(ResourceLocatorSubresource3Interface.class);
        return TestUtil.finishContainerPrepare(war, null,
                ResourceLocatorAbstractAnnotationFreeResouce.class,
                ResourceLocatorAnnotationFreeSubResource.class,
                ResourceLocatorBaseResource.class,
                ResourceLocatorCollectionResource.class,
                ResourceLocatorDirectory.class,
                ResourceLocatorSubresource.class,
                ResourceLocatorSubresource2.class,
                ResourceLocatorSubresource3.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ResourceLocatorTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Resource locator returns proxied resource.
     * @tpSince RESTEasy 3.0.20
     */
    @Test
    public void testProxiedSubresource() throws Exception {
        WebTarget target = client.target(generateURL("/proxy/3"));
        Response res = target.queryParam("foo", "1.2").queryParam("foo", "1.3").request().get();
        Assertions.assertEquals(200, res.getStatus());
        res.close();
    }

    /**
     * @tpTestDetails 1) Resource locator returns resource; 2) Resource locator returns resource locator.
     * @tpSince RESTEasy 3.0.20
     */
    @Test
    public void testSubresource() throws Exception {
        {
            Response response = client.target(generateURL("/base/1/resources")).request().get();
            Assertions.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
            Assertions.assertEquals(ResourceLocatorSubresource.class.getName(), response.readEntity(String.class));
        }

        {
            Response response = client.target(generateURL("/base/1/resources/subresource2/stuff/2/bar")).request().get();
            Assertions.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
            Assertions.assertEquals(ResourceLocatorSubresource2.class.getName() + "-2", response.readEntity(String.class));
        }
    }

    /**
     * @tpTestDetails Two matching metods, one a resource locator, the other a resource method.
     * @tpSince RESTEasy 3.0.20
     */
    @Test
    public void testSameUri() throws Exception {
        Response response = client.target(generateURL("/directory/receivers/1")).request().delete();
        Assertions.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
        Assertions.assertEquals(ResourceLocatorDirectory.class.getName(), response.readEntity(String.class));
    }

    /**
     * @tpTestDetails Locator returns resource which inherits annotations from an interface.
     * @tpSince RESTEasy 3.0.20
     */
    @Test
    public void testAnnotationFreeSubresource() throws Exception {
        {
            Response response = client.target(generateURL("/collection/annotation_free_subresource")).request().get();
            Assertions.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
            Assertions.assertEquals("got", response.readEntity(String.class));
            Assertions.assertNotNull(response.getHeaderString("Content-Type"));
            Assertions.assertNotNull(response.getHeaderString("Content-Type"));
            Assertions.assertEquals(MediaType.TEXT_PLAIN_TYPE.withCharset("UTF-8").toString(),
                    response.getHeaderString("Content-Type"));
        }

        {
            Builder request = client.target(generateURL("/collection/annotation_free_subresource")).request();
            Response response = request.post(Entity.entity("hello!".getBytes(), MediaType.TEXT_PLAIN));
            Assertions.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
            Assertions.assertEquals("posted: hello!", response.readEntity(String.class));
        }
    }
}
