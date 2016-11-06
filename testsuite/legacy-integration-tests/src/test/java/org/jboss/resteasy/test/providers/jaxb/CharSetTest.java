package org.jboss.resteasy.test.providers.jaxb;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.test.providers.jaxb.resource.CharSetResource;
import org.jboss.resteasy.test.providers.jaxb.resource.CharSetCustomer;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.resteasy.utils.PortProviderUtil;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @tpSubChapter Jaxb provider
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class CharSetTest {

    private final Logger logger = Logger.getLogger(CharSetResource.class.getName());
    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(CharSetTest.class.getSimpleName());
        war.addClass(CharSetTest.class);
        return TestUtil.finishContainerPrepare(war, null, CharSetCustomer.class, CharSetResource.class);
    }

    @Before
    public void init() {
        client = new ResteasyClientBuilder().build();
    }

    @After
    public void after() throws Exception {
        client.close();
        client = null;
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, CharSetTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Client sends POST request with jaxb annotated object entity and the targeted resource receives jaxb
     * object with corect encoding.
     * @tpPassCrit The jaxb object xml element is same as original
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testReceiveJaxbObjectAsItis() throws Exception {
        ResteasyWebTarget target = client.target(generateURL("/test/string"));
        CharSetCustomer cust = new CharSetCustomer();
        String name = "bill\u00E9";
        cust.setName(name);
        Response response = target.request().accept("application/xml").post(Entity.entity(cust, MediaType.APPLICATION_XML_TYPE));
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        response.close();
    }

    /**
     * @tpTestDetails Client sends POST request with jaxb annotated object entity and the targeted resource receives
     * xml string.
     * @tpPassCrit Jaxb object is unmarshalled to the expected xml string with correct encoding
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testReceiveJaxbObjectAsString() throws Exception {
        ResteasyWebTarget target = client.target(generateURL("/test"));
        CharSetCustomer cust = new CharSetCustomer();
        String name = "bill\u00E9";
        logger.info("client name: " + name);
        logger.info("bytes string: " + new String(name.getBytes("UTF-8"), "UTF-8"));
        cust.setName(name);
        Response response = target.request().accept("application/xml").post(Entity.entity(cust, MediaType.APPLICATION_XML_TYPE));
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        response.close();
    }
}
