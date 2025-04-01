package org.jboss.resteasy.test.client.other;

import java.net.URI;
import java.net.URLPermission;

import jakarta.ws.rs.client.ClientBuilder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.engine.ClientHttpEngineFactory;
import org.jboss.resteasy.test.client.other.resource.ApacheHttpClient4Resource;
import org.jboss.resteasy.test.client.other.resource.ApacheHttpClient4ResourceImpl;
import org.jboss.resteasy.test.client.other.resource.CustomHttpClientEngineFactory;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.wildfly.arquillian.junit.annotations.RequiresModule;
import org.wildfly.testing.tools.deployments.DeploymentDescriptors;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpTestCaseDetails Client engine customization (RESTEASY-1599)
 * @tpSince RESTEasy 3.0.24
 */
@ExtendWith(ArquillianExtension.class)
@RequiresModule(value = "org.jboss.resteasy.resteasy-client-api", minVersion = "6.2.11.Final")
public class CustomHttpClientEngineTest {

    @ArquillianResource
    private URI uri;

    @Deployment
    public static Archive<?> deploySimpleResource() {
        WebArchive war = TestUtil.prepareArchive(CustomHttpClientEngineTest.class.getSimpleName())
                .addClasses(CustomHttpClientEngineFactory.class,
                        CustomHttpClientEngineFactory.CustomAsyncHttpClientEngine.class)
                .addAsServiceProvider(ClientHttpEngineFactory.class, CustomHttpClientEngineFactory.class);
        war.addClass(ApacheHttpClient4Resource.class)
                .addAsManifestResource(DeploymentDescriptors.createPermissionsXmlAsset(
                        new URLPermission("http://127.0.0.1:8080/-")), "permissions.xml");
        return TestUtil.finishContainerPrepare(war, null, ApacheHttpClient4ResourceImpl.class);
    }

    /**
     * @tpTestDetails Create custom ClientHttpEngine and set it to the resteasy-client
     * @tpSince RESTEasy 3.0.24
     */
    @Test
    public void test() throws Exception {
        ResteasyClientBuilder clientBuilder = ((ResteasyClientBuilder) ClientBuilder.newBuilder());
        ResteasyClient client = clientBuilder.build();
        Assertions.assertInstanceOf(CustomHttpClientEngineFactory.CustomAsyncHttpClientEngine.class, client.httpEngine());

        ApacheHttpClient4Resource proxy = client.target(uri).proxy(ApacheHttpClient4Resource.class);
        Assertions.assertEquals("hello world", proxy.get(), "Unexpected response");

        client.close();
    }
}
