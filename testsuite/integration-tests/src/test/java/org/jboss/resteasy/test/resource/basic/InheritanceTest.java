package org.jboss.resteasy.test.resource.basic;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation.Builder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.resource.basic.resource.InheritenceParentResource;
import org.jboss.resteasy.test.resource.basic.resource.InheritenceParentResourceImpl;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Resource
 * @tpChapter Integration tests
 * @tpTestCaseDetails Tests annotation inheritence from interface.
 * @tpSince RESTEasy 3.0.20
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class InheritanceTest {
    private static Client client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(InheritanceTest.class.getSimpleName());
        war.addClass(InheritenceParentResource.class);
        return TestUtil.finishContainerPrepare(war, null, InheritenceParentResourceImpl.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, InheritanceTest.class.getSimpleName());
    }

    @BeforeAll
    public static void beforeSub() {
        client = ClientBuilder.newClient();
    }

    @AfterAll
    public static void afterSub() {
        client.close();
    }

    @Test
    public void Test1() throws Exception {
        Builder builder = client.target(generateURL("/InheritanceTest")).request();
        builder.header("Accept", "text/plain");
        Response response = builder.get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("First", response.readEntity(String.class));
    }
}
