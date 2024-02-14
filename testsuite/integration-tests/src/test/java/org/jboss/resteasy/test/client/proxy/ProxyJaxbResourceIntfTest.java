package org.jboss.resteasy.test.client.proxy;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ProxyBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.client.proxy.resource.ProxyJaxbCredit;
import org.jboss.resteasy.test.client.proxy.resource.ProxyJaxbResource;
import org.jboss.resteasy.test.client.proxy.resource.ProxyJaxbResourceIntf;
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
public class ProxyJaxbResourceIntfTest {

    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ProxyJaxbResourceIntfTest.class.getSimpleName());
        war.addClass(ProxyJaxbResourceIntfTest.class);
        war.addClass(ProxyJaxbResourceIntf.class);
        return TestUtil.finishContainerPrepare(war, null, ProxyJaxbResource.class,
                ProxyJaxbCredit.class);
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
        return PortProviderUtil.generateURL(path, ProxyJaxbResourceIntfTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Tests client proxy with annotated jaxb resource, RESTEASY-306
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testIt() throws Exception {
        ProxyJaxbResourceIntf proxy = ProxyBuilder.builder(ProxyJaxbResourceIntf.class, client.target(generateURL("/")))
                .build();
        Response response = proxy.getCredits("xx");
        Assertions.assertEquals(response.getStatus(), HttpResponseCodes.SC_OK);
        ProxyJaxbCredit cred = response.readEntity(ProxyJaxbCredit.class);
        Assertions.assertEquals("foobar", cred.getName(),
                "Unexpected response from the server");
    }

}
