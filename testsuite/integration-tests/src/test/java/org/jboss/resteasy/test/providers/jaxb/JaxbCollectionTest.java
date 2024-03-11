package org.jboss.resteasy.test.providers.jaxb;

import java.util.List;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.providers.jaxb.resource.JaxbCollectionFoo;
import org.jboss.resteasy.test.providers.jaxb.resource.JaxbCollectionNamespacedFoo;
import org.jboss.resteasy.test.providers.jaxb.resource.JaxbCollectionNamespacedResource;
import org.jboss.resteasy.test.providers.jaxb.resource.JaxbCollectionResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Jaxb provider
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class JaxbCollectionTest {

    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(JaxbCollectionTest.class.getSimpleName());
        war.addClass(JaxbCollectionTest.class);
        return TestUtil.finishContainerPrepare(war, null, JaxbCollectionResource.class, JaxbCollectionNamespacedResource.class,
                JaxbCollectionFoo.class, JaxbCollectionNamespacedFoo.class);
    }

    @BeforeEach
    public void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterEach
    public void after() throws Exception {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, JaxbCollectionTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Client sends POST request with xml entity, the request is processed by resource, which can process
     *                JAXB objects wrapped in collection element.
     * @tpPassCrit The Response contains correct number of elements and correct values
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testNakedArray() throws Exception {
        String xml = "<resteasy:collection xmlns:resteasy=\"http://jboss.org/resteasy\">"
                + "<foo test=\"hello\"/></resteasy:collection>";

        ResteasyWebTarget target = client.target(generateURL("/array"));
        Response response = target.request().accept("application/xml").post(Entity.xml(xml));
        List<JaxbCollectionFoo> list = response.readEntity(new jakarta.ws.rs.core.GenericType<List<JaxbCollectionFoo>>() {
        });
        Assertions.assertEquals(1, list.size(), "The response doesn't contain 1 item, which is expected");
        Assertions.assertEquals(list.get(0).getTest(), "hello", "The response doesn't contain correct element value");
        response.close();
    }

    /**
     * @tpTestDetails Client sends POST request with xml entity, the request is processed by resource, which can process
     *                JAXB objects wrapped in collection element. The resource has changed the collection element name
     *                using @Wrapped
     *                annotation on the resource to 'list'.
     * @tpPassCrit The Response contains correct number of elements and correct values
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testList() throws Exception {
        String xml = "<list>"
                + "<foo test=\"hello\"/></list>";

        ResteasyWebTarget target = client.target(generateURL("/list"));
        Response response = target.request().post(Entity.xml(xml));
        JaxbCollectionFoo[] list = response.readEntity(new jakarta.ws.rs.core.GenericType<JaxbCollectionFoo[]>() {
        });
        Assertions.assertEquals(1, list.length, "The response doesn't contain 1 item, which is expected");
        Assertions.assertEquals(list[0].getTest(), "hello", "The response doesn't contain correct element value");
        response.close();

    }

    /**
     * @tpTestDetails Client sends POST request with xml entity, the request is processed by resource, which can process
     *                JAXB objects wrapped in collection element. The XML element of name 'foo' has changed namespace to
     *                'http://foo.com'.
     * @tpPassCrit The Response contains correct number of elements and correct values
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testNamespacedNakedArray() throws Exception {
        String xml = "<collection xmlns:foo=\"http://foo.com\">"
                + "<foo:foo test=\"hello\"/></collection>";

        ResteasyWebTarget target = client.target(generateURL("/namespaced/array"));
        Response response = target.request().post(Entity.xml(xml));
        List<JaxbCollectionNamespacedFoo> list = response
                .readEntity(new jakarta.ws.rs.core.GenericType<List<JaxbCollectionNamespacedFoo>>() {
                });
        Assertions.assertEquals(1, list.size(), "The response doesn't contain 1 item, which is expected");
        Assertions.assertEquals(list.get(0).getTest(), "hello", "The response doesn't contain correct element value");
        response.close();

    }

    /**
     * @tpTestDetails Client sends POST request with xml entity, the request is processed by resource, which can process
     *                JAXB objects wrapped in collection element. The resource has changed the collection element name
     *                using @Wrapped
     *                annotation on the resource to 'list'. The XML element of name 'foo' has changed namespace to
     *                'http://foo.com'.
     * @tpPassCrit The Response contains correct number of elements and correct values
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testNamespacedList() throws Exception {
        String xml = "<list xmlns:foo=\"http://foo.com\">"
                + "<foo:foo test=\"hello\"/></list>";

        ResteasyWebTarget target = client.target(generateURL("/namespaced/list"));
        Response response = target.request().post(Entity.xml(xml));
        JaxbCollectionNamespacedFoo[] list = response
                .readEntity(new jakarta.ws.rs.core.GenericType<JaxbCollectionNamespacedFoo[]>() {
                });
        Assertions.assertEquals(1, list.length, "The response doesn't contain 1 item, which is expected");
        Assertions.assertEquals(list[0].getTest(), "hello", "The response doesn't contain correct element value");
        response.close();
    }

    /**
     * @tpTestDetails Client sends POST request with xml entity containing wrong element name for collection.
     * @tpPassCrit Response with code BAD REQUEST
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testBadList() throws Exception {
        String xml = "<bad-list>"
                + "<foo test=\"hello\"/></bad-list>";

        ResteasyWebTarget target = client.target(generateURL("/list"));
        Response response = target.request().post(Entity.xml(xml));
        Assertions.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
        response.close();
    }

}
