package org.jboss.resteasy.test.core.basic;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.test.core.basic.resource.NullHeaderFilter;
import org.jboss.resteasy.test.core.basic.resource.NullHeaderResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter RESTEASY-1565
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.1.1.Final
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class NullHeaderTest {

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(NullHeaderTest.class.getSimpleName());
        war.addClass(NullHeaderFilter.class);
        return TestUtil.finishContainerPrepare(war, null, NullHeaderResource.class);
    }

    @Test
    public void testNullHeader() throws Exception {

        Client client = ClientBuilder.newClient();
        WebTarget base = client.target(PortProviderUtil.generateURL("/test", NullHeaderTest.class.getSimpleName()));
        Response response = base.register(NullHeaderFilter.class).request().header("X-Auth-User", null).get();
        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatus());
        String serverHeader = response.getHeaderString("X-Server-Header");
        Assertions.assertTrue(serverHeader == null || "".equals(serverHeader));
        client.close();
    }
}
