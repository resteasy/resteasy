package org.jboss.resteasy.test.providers.jaxb;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.test.providers.jaxb.resource.EmptyContentTypeFoo;
import org.jboss.resteasy.test.providers.jaxb.resource.EmptyContentTypeResource;
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

/**
 * @tpSubChapter Jaxb provider
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class EmptyContentTypeTest {

    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(EmptyContentTypeTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, EmptyContentTypeResource.class, EmptyContentTypeFoo.class);
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
        return PortProviderUtil.generateURL(path, EmptyContentTypeTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Test for the resource with two post methods, one consumes xml content type the other consumes empty
     * content type
     * @tpInfo RESTEASY-518
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testEmptyContentType() throws Exception {
        ResteasyWebTarget target = client.target(generateURL("/test"));
        EmptyContentTypeFoo foo = new EmptyContentTypeFoo();
        foo.setName("Bill");
        Response response = target.request().post(Entity.entity(foo, "application/xml"));
        Assert.assertEquals("The response from the server doesn't match the expected one",
                response.readEntity(String.class), "Bill");

        Response response2 = target.request().post(null);
        Assert.assertEquals("The response from the server doesn't match the expected one",
                response2.readEntity(String.class), "NULL");
    }

}
