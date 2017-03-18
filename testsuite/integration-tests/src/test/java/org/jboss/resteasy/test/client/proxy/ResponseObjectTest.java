package org.jboss.resteasy.test.client.proxy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ProxyBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.client.proxy.resource.ResponseObjectBasicObjectIntf;
import org.jboss.resteasy.test.client.proxy.resource.ResponseObjectClientIntf;
import org.jboss.resteasy.test.client.proxy.resource.ResponseObjectHateoasObject;
import org.jboss.resteasy.test.client.proxy.resource.ResponseObjectResource;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.Assert;
import org.junit.runner.RunWith;

import javax.ws.rs.ProcessingException;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ResponseObjectTest {

    protected static final Logger logger = LogManager.getLogger(ResponseObjectTest.class.getName());
    static ResteasyClient client;
    ResponseObjectClientIntf responseObjectClientIntf;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ResponseObjectTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, ResponseObjectResource.class);
    }

    @Before
    public void init() {
        client = new ResteasyClientBuilder().build();
        responseObjectClientIntf = ProxyBuilder.builder(ResponseObjectClientIntf.class, client.target(generateURL(""))).build();
    }

    @After
    public void after() throws Exception {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ResponseObjectTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Tests ResponseObject annotation on a client interface, invoking the request with ProxyBuilder instance
     * @tpPassCrit The response contains the expected header
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testSimpleProxyBuilder() {
        ResponseObjectBasicObjectIntf obj = responseObjectClientIntf.get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, obj.status());
        Assert.assertEquals("The response object doesn't contain the expected string", "ABC", obj.body());
        try {
            Assert.assertEquals("The response object doesn't contain the expected header",
                    "text/plain;charset=UTF-8", obj.response().getHeaders().getFirst("Content-Type"));
        } catch (ClassCastException ex) {
            Assert.fail(TestUtil.getErrorMessageForKnownIssue("JBEAP-2446"));
        }
        Assert.assertEquals("The response object doesn't contain the expected header", "text/plain;charset=UTF-8", obj.contentType());
    }

    /**
     * @tpTestDetails Tests ResponseObject annotation on a client interface, and resource containg a Link object,
     * forwarding to another resource and invoking the request with ProxyBuilder instance
     * @tpPassCrit The request was forwarded to another resource
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testLinkFollowProxyBuilder() {
        ResponseObjectHateoasObject obj = responseObjectClientIntf.performGetBasedOnHeader();
        Assert.assertEquals(HttpResponseCodes.SC_NO_CONTENT, obj.status());
        Assert.assertTrue("The resource was not forwarded", obj.nextLink().getPath().endsWith("next-link"));
        try {
            Assert.assertEquals("The resource was not forwarded", "forwarded", obj.followNextLink());
        } catch (ProcessingException ex) {
            Assert.fail(TestUtil.getErrorMessageForKnownIssue("JBEAP-2446"));
        }
    }
}
