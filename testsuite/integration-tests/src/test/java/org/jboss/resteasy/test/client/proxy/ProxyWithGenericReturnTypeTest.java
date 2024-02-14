package org.jboss.resteasy.test.client.proxy;

import java.util.ArrayList;
import java.util.List;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.client.proxy.resource.ProxyWithGenericReturnTypeInvocationHandler;
import org.jboss.resteasy.test.client.proxy.resource.ProxyWithGenericReturnTypeMessageBodyWriter;
import org.jboss.resteasy.test.client.proxy.resource.ProxyWithGenericReturnTypeResource;
import org.jboss.resteasy.test.client.proxy.resource.ProxyWithGenericReturnTypeSubResourceIntf;
import org.jboss.resteasy.test.client.proxy.resource.ProxyWithGenericReturnTypeSubResourceSubIntf;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Client tests
 * @tpTestCaseDetails Test for generic proxy return type. Proxy is set on server (not on client).
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ProxyWithGenericReturnTypeTest {

    @Deployment
    public static Archive<?> deploySimpleResource() {
        WebArchive war = TestUtil.prepareArchive(ProxyWithGenericReturnTypeTest.class.getSimpleName());

        war.addClass(ProxyWithGenericReturnTypeInvocationHandler.class);
        war.addClass(ProxyWithGenericReturnTypeSubResourceIntf.class);
        war.addClass(ProxyWithGenericReturnTypeSubResourceSubIntf.class);

        List<Class<?>> singletons = new ArrayList<>();
        singletons.add(ProxyWithGenericReturnTypeMessageBodyWriter.class);
        return TestUtil.finishContainerPrepare(war, null, singletons, ProxyWithGenericReturnTypeResource.class);
    }

    /**
     * @tpTestDetails Test for new client
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void newClientTest() throws Exception {
        Client client = ClientBuilder.newClient();

        WebTarget base = client
                .target(PortProviderUtil.generateURL("/test/list/", ProxyWithGenericReturnTypeTest.class.getSimpleName()));
        Response response = base.request().get();

        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertTrue(response.readEntity(String.class).indexOf("List<String>") >= 0,
                "Wrong content of response, list was not decoden on server");

        client.close();
    }
}
