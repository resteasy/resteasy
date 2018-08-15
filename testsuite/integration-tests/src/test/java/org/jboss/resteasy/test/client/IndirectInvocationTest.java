package org.jboss.resteasy.test.client;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.client.resource.IndirectInvocationTestResource;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.Response;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author <a href="mailto:kanovotn@redhat.com">Katerina Novotna</a>
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 *
 */
@RunWith(Arquillian.class)
@RunAsClient
public class IndirectInvocationTest extends ClientTestBase{

    public static final int REPEAT = 15;

    static Client client;

    @Before
    public void before() {
        client = ClientBuilder.newClient();
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(IndirectInvocationTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, IndirectInvocationTestResource.class);
    }

    @AfterClass
    public static void close() {
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
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("123456 3", response.readEntity(String.class));
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
                Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
                response.close();
            }
        }
    }

}
