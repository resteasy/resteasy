package org.jboss.resteasy.test.providers.jaxb;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.test.providers.jaxb.resource.JaxbCollectionFoo;
import org.jboss.resteasy.test.providers.jaxb.resource.JaxbCollectionNamespacedFoo;
import org.jboss.resteasy.test.providers.jaxb.resource.JaxbCollectionNamespacedResource;
import org.jboss.resteasy.test.providers.jaxb.resource.JaxbCollectionResource;
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

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * @tpSubChapter Jaxb provider
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
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

    @Before
    public void init() {
        client = new ResteasyClientBuilder().build();
    }

    @After
    public void after() throws Exception {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, JaxbCollectionTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Client sends POST request with xml entity, the request is processed by resource, which can process
     * JAXB objects wrapped in collection element.
     * @tpPassCrit The Response contains correct number of elements and correct values
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testNakedArray() throws Exception {
        String xml = "<resteasy:collection xmlns:resteasy=\"http://jboss.org/resteasy\">"
                + "<foo test=\"hello\"/></resteasy:collection>";

        ResteasyWebTarget target = client.target(generateURL("/array"));
        Response response = target.request().accept("application/xml").post(Entity.xml(xml));
        List<JaxbCollectionFoo> list = response.readEntity(new javax.ws.rs.core.GenericType<List<JaxbCollectionFoo>>() {
        });
        Assert.assertEquals("The response doesn't contain 1 item, which is expected", 1, list.size());
        Assert.assertEquals("The response doesn't contain correct element value", list.get(0).getTest(), "hello");
        response.close();
    }

    /**
     * @tpTestDetails Client sends POST request with xml entity, the request is processed by resource, which can process
     * JAXB objects wrapped in collection element. The resource has changed the collection element name using @Wrapped
     * annotation on the resource to 'list'.
     * @tpPassCrit The Response contains correct number of elements and correct values
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testList() throws Exception {
        String xml = "<list>"
                + "<foo test=\"hello\"/></list>";

        ResteasyWebTarget target = client.target(generateURL("/list"));
        Response response = target.request().post(Entity.xml(xml));
        JaxbCollectionFoo[] list = response.readEntity(new javax.ws.rs.core.GenericType<JaxbCollectionFoo[]>() {
        });
        Assert.assertEquals("The response doesn't contain 1 item, which is expected", 1, list.length);
        Assert.assertEquals("The response doesn't contain correct element value", list[0].getTest(), "hello");
        response.close();

    }

    /**
     * @tpTestDetails Client sends POST request with xml entity, the request is processed by resource, which can process
     * JAXB objects wrapped in collection element. The XML element of name 'foo' has changed namespace to 'http://foo.com'.
     * @tpPassCrit The Response contains correct number of elements and correct values
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testNamespacedNakedArray() throws Exception {
        String xml = "<collection xmlns:foo=\"http://foo.com\">"
                + "<foo:foo test=\"hello\"/></collection>";

        ResteasyWebTarget target = client.target(generateURL("/namespaced/array"));
        Response response = target.request().post(Entity.xml(xml));
        List<JaxbCollectionNamespacedFoo> list = response.readEntity(new javax.ws.rs.core.GenericType<List<JaxbCollectionNamespacedFoo>>() {
        });
        Assert.assertEquals("The response doesn't contain 1 item, which is expected", 1, list.size());
        Assert.assertEquals("The response doesn't contain correct element value", list.get(0).getTest(), "hello");
        response.close();

    }

    /**
     * @tpTestDetails Client sends POST request with xml entity, the request is processed by resource, which can process
     * JAXB objects wrapped in collection element. The resource has changed the collection element name using @Wrapped
     * annotation on the resource to 'list'. The XML element of name 'foo' has changed namespace to 'http://foo.com'.
     * @tpPassCrit The Response contains correct number of elements and correct values
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testNamespacedList() throws Exception {
        String xml = "<list xmlns:foo=\"http://foo.com\">"
                + "<foo:foo test=\"hello\"/></list>";

        ResteasyWebTarget target = client.target(generateURL("/namespaced/list"));
        Response response = target.request().post(Entity.xml(xml));
        JaxbCollectionNamespacedFoo[] list = response.readEntity(new javax.ws.rs.core.GenericType<JaxbCollectionNamespacedFoo[]>() {
        });
        Assert.assertEquals("The response doesn't contain 1 item, which is expected", 1, list.length);
        Assert.assertEquals("The response doesn't contain correct element value", list[0].getTest(), "hello");
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
        Assert.assertEquals(HttpResponseCodes.SC_BAD_REQUEST, response.getStatus());
        response.close();
    }

}
