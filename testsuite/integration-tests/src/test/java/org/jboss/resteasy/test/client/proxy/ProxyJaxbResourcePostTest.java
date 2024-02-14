package org.jboss.resteasy.test.client.proxy;

import java.math.BigDecimal;
import java.util.Date;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ProxyBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.client.proxy.resource.ProxyJaxbResourceMessageResource;
import org.jboss.resteasy.test.client.proxy.resource.ProxyJaxbResourcePostMessage;
import org.jboss.resteasy.test.client.proxy.resource.ProxyJaxbResourcePostMessageIntf;
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
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
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

    @BeforeEach
    public void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterEach
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
        ProxyJaxbResourcePostMessageIntf proxy = ProxyBuilder
                .builder(ProxyJaxbResourcePostMessageIntf.class, client.target(generateURL("/"))).build();
        Response r = proxy.saveMessage(m);
        Assertions.assertEquals(r.getStatus(), HttpResponseCodes.SC_CREATED);
    }
}
