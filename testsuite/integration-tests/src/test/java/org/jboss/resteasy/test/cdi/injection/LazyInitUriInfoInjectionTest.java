package org.jboss.resteasy.test.cdi.injection;

import java.net.URI;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.test.cdi.injection.resource.LazyInitUriInfoInjectionResource;
import org.jboss.resteasy.test.cdi.injection.resource.LazyInitUriInfoInjectionSingletonResource;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter Injection
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-573
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class LazyInitUriInfoInjectionTest {

    @Deployment
    public static Archive<?> deploySimpleResource() {
        WebArchive war = TestUtil.prepareArchive(LazyInitUriInfoInjectionTest.class.getSimpleName())
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        return TestUtil.finishContainerPrepare(war, null, LazyInitUriInfoInjectionSingletonResource.class,
                LazyInitUriInfoInjectionResource.class);
    }

    @ArquillianResource
    URI baseUri;

    private String generateURL(String path) {
        return baseUri.resolve(path).toString();
    }

    /**
     * @tpTestDetails Repeat client request without query parameter
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testDup() throws Exception {
        ResteasyClient client = (ResteasyClient) ClientBuilder.newClient();
        WebTarget base = client.target(generateURL("test?h=world"));
        String val = base.request().get().readEntity(String.class);
        Assert.assertEquals(val, "world");

        base = client.target(generateURL("test"));
        val = base.request().get().readEntity(String.class);
        Assert.assertEquals(val, "");
        client.close();
    }
}
