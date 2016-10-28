package org.jboss.resteasy.test.resource.basic;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.resource.basic.resource.GenericEntityDoubleWriter;
import org.jboss.resteasy.test.resource.basic.resource.GenericEntityResource;
import org.jboss.resteasy.test.resource.basic.resource.GenericEntitytFloatWriter;
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

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

/**
 * @tpSubChapter Resource
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class GenericEntityTest {

    static Client client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(GenericEntityTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, GenericEntityResource.class, GenericEntityDoubleWriter.class,
                GenericEntitytFloatWriter.class);
    }

    @BeforeClass
    public static void init() {
        client = ClientBuilder.newClient();
    }

    @AfterClass
    public static void after() throws Exception {
        client.close();
        client = null;
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, GenericEntityTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Resource returning GenericEntity with custom MessageBodyWriter returning double values
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testDoubles() {
        WebTarget base = client.target(generateURL("/doubles"));
        try {
            Response response = base.request().get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            String body = response.readEntity(String.class);
            Assert.assertEquals("The response doesn't contain the expected entity", "45.0D 50.0D ", body);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @tpTestDetails Resource returning GenericEntity with custom MessageBodyWriter returning float values
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testFloats() {
        WebTarget base = client.target(generateURL("/floats"));
        try {
            Response response = base.request().get();
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            String body = response.readEntity(String.class);
            Assert.assertEquals("The response doesn't contain the expected entity", "45.0F 50.0F ", body);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
