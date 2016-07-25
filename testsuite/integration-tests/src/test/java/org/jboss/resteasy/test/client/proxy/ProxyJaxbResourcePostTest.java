package org.jboss.resteasy.test.client.proxy;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ProxyBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.client.proxy.resource.ProxyJaxbResourceMessageResource;
import org.jboss.resteasy.test.client.proxy.resource.ProxyJaxbResourcePostMessage;
import org.jboss.resteasy.test.client.proxy.resource.ProxyJaxbResourcePostMessageIntf;
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

import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.Date;


/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ProxyJaxbResourcePostTest {

    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ProxyJaxbResourcePostTest.class.getSimpleName());
        war.addClass(ProxyJaxbResourcePostMessageIntf.class);
        return TestUtil.finishContainerPrepare(war, null, ProxyJaxbResourcePostMessage.class,
                ProxyJaxbResourceMessageResource.class);
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
        return PortProviderUtil.generateURL(path, ProxyJaxbResourcePostTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Tests client proxy with annotated jaxb resource, sends jaxb object to the server
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testClient() throws Exception {
        ProxyJaxbResourcePostMessage m = new ProxyJaxbResourcePostMessage();
        m.setMsgId(new BigDecimal(42));
        m.setCreatedDate(new Date());
        m.setDestinationId("ABCD1234");
        m.setMsgComp(new BigDecimal(2));
        m.setNumLocTfmsProvided(new BigDecimal(14));
        m.setSourceId("WXYZ6789");
        m.setVersionMajor("4");
        m.setVersionMinor("1");
        ProxyJaxbResourcePostMessageIntf proxy = ProxyBuilder.builder(ProxyJaxbResourcePostMessageIntf.class, client.target(generateURL("/"))).build();
        Response r = proxy.saveMessage(m);
        Assert.assertEquals(r.getStatus(), HttpResponseCodes.SC_CREATED);
    }
}
