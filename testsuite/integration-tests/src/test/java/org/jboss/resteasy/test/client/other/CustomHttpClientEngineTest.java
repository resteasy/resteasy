package org.jboss.resteasy.test.client.other;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ClientHttpEngine;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient43Engine;
import org.jboss.resteasy.test.client.other.resource.ApacheHttpClient4Resource;
import org.jboss.resteasy.test.client.other.resource.ApacheHttpClient4ResourceImpl;
import org.jboss.resteasy.test.client.other.resource.CustomHttpClientEngineBuilder;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpTestCaseDetails Client engine customization (RESTEASY-1599)
 * @tpSince RESTEasy 3.0.24
 */
@RunWith(Arquillian.class)
@RunAsClient
public class CustomHttpClientEngineTest {

    @Deployment
    public static Archive<?> deploySimpleResource() {
        WebArchive war = TestUtil.prepareArchive(CustomHttpClientEngineTest.class.getSimpleName());
        war.addClass(ApacheHttpClient4Resource.class);
        return TestUtil.finishContainerPrepare(war, null, ApacheHttpClient4ResourceImpl.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, CustomHttpClientEngineTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Create custom ClientHttpEngine and set it to the resteasy-client
     * @tpSince RESTEasy 3.0.24
     */
    @Test
    public void test() {
        ResteasyClientBuilder clientBuilder = new ResteasyClientBuilder();
        ClientHttpEngine engine = new CustomHttpClientEngineBuilder().resteasyClientBuilder(clientBuilder).build();
        ResteasyClient client = clientBuilder.httpEngine(engine).build();
        Assert.assertTrue(ApacheHttpClient43Engine.class.isInstance(client.httpEngine()));

        ApacheHttpClient4Resource proxy = client.target(generateURL("")).proxy(ApacheHttpClient4Resource.class);
        Assert.assertEquals("Unexpected response", "hello world", proxy.get());

        client.close();
    }
}
