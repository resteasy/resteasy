package org.jboss.resteasy.test.interceptor;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.test.client.ClientTestBase;
import org.jboss.resteasy.test.interceptor.resource.PreMatchingClientRequestFilterImpl;
import org.jboss.resteasy.test.interceptor.resource.PreMatchingClientResource;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Interceptor
 * @tpChapter Integration tests
 * @tpTestCaseDetails Tests @PreMatching annotation on ClientRequestFilter (RESTEASY-1696)
 * @tpSince RESTEasy 4.0.0
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class PreMatchingClientRequestFilterTest extends ClientTestBase {

    static Client client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(PreMatchingClientRequestFilterTest.class.getSimpleName());
        //rls //war.addClass(ClientExceptionsData.class);
        return TestUtil.finishContainerPrepare(war, null, PreMatchingClientResource.class);
    }

    @BeforeEach
    public void before() {
        client = ClientBuilder.newClient();
    }

    @AfterEach
    public void close() {
        client.close();
    }

    /**
     * @tpTestDetails Test that annotation @PreMatching on an implementation of ClientRequestFilter
     *                is ignored. This annotation is only valid on ContainerRequestFilter implementations.
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void preMatchingTest() throws Exception {
        WebTarget base = client.target(generateURL("/") + "testIt");
        Response response = base.register(PreMatchingClientRequestFilterImpl.class).request().get();
        Assertions.assertEquals(404, response.getStatus());
    }

}
