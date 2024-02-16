package org.jboss.resteasy.test.client.other;

import jakarta.ws.rs.client.ClientBuilder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpTestCaseDetails Client engine customization (RESTEASY-1599)
 * @tpSince RESTEasy 3.0.24
 */
@ExtendWith(ArquillianExtension.class)
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
        ResteasyClientBuilder clientBuilder = ((ResteasyClientBuilder) ClientBuilder.newBuilder());
        ClientHttpEngine engine = new CustomHttpClientEngineBuilder().resteasyClientBuilder(clientBuilder).build();
        ResteasyClient client = clientBuilder.httpEngine(engine).build();
        Assertions.assertTrue(ApacheHttpClient43Engine.class.isInstance(client.httpEngine()));

        ApacheHttpClient4Resource proxy = client.target(generateURL("")).proxy(ApacheHttpClient4Resource.class);
        Assertions.assertEquals("hello world", proxy.get(), "Unexpected response");

        client.close();
    }
}
