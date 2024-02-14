package org.jboss.resteasy.test.client;

import java.util.Arrays;
import java.util.Collection;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.client.resource.IndirectInvocationTestResource;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @author <a href="mailto:kanovotn@redhat.com">Katerina Novotna</a>
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 *
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class IndirectInvocationTest extends ClientTestBase {

    public static final int REPEAT = 15;

    Client client;

    @BeforeEach
    public void before() {
        client = ClientBuilder.newClient();
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(IndirectInvocationTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, IndirectInvocationTestResource.class);
    }

    @AfterEach
    public void close() {
        client.close();
    }

    /**
     * @tpTestDetails Create Invocation request and submit it using invoke() method, verify the answer
     * @tpPassCrit Expected response is returned from the server
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void invokeLaterTest() {
        Invocation inv = client.target(generateURL("/") + "test/query")
                .queryParam("param", "123456")
                .queryParam("id", "3")
                .request("text/plain").buildGet();

        Response response = inv.invoke();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("123456 3", response.readEntity(String.class));
    }

    /**
     * @tpTestDetails Create two Invocations requests, store them in the list and then call them multiple times
     * @tpPassCrit Expected response is returned from the server
     * @tpInfo https://weblogs.java.net/blog/spericas/archive/2011/10/20/jax-rs-20-client-api-generic-interface
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void invokeMultipleTimes() {
        Invocation inv1 = client.target(generateURL("/") + "test/query")
                .queryParam("param", "123456")
                .queryParam("id", "3")
                .request("text/plain").buildGet();

        Invocation inv2 = client.target(generateURL("/") + "test/send")
                .queryParam("param", "123456")
                .queryParam("id", "3")
                .request("text/plain").buildPost(Entity.text("50.0"));

        Collection<Invocation> invs = Arrays.asList(inv1, inv2);

        for (int i = 0; i < REPEAT; i++) {
            for (Invocation inv : invs) {
                Response response = inv.invoke();
                Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
                response.close();
            }
        }
    }

}
