package org.jboss.resteasy.test.providers.custom;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.providers.custom.resource.ProviderContextInjectionAnyExceptionExceptionMapper;
import org.jboss.resteasy.test.providers.custom.resource.ProviderContextInjectionEnumContextResolver;
import org.jboss.resteasy.test.providers.custom.resource.ProviderContextInjectionEnumProvider;
import org.jboss.resteasy.test.providers.custom.resource.ProviderContextInjectionIOExceptionExceptionMapper;
import org.jboss.resteasy.test.providers.custom.resource.ProviderContextInjectionResource;
import org.jboss.resteasy.test.providers.custom.resource.ProviderContextInjectionTextPlainEnumContextResolver;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ProviderContextInjectionTest {

    static Client client;

    @BeforeClass
    public static void setup() {
        client = ClientBuilder.newClient();
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ProviderContextInjectionTest.class.getSimpleName());
        war.addClasses(ProviderContextInjectionEnumProvider.class);
        return TestUtil.finishContainerPrepare(war, null, ProviderContextInjectionResource.class,
                ProviderContextInjectionAnyExceptionExceptionMapper.class, ProviderContextInjectionIOExceptionExceptionMapper.class,
                ProviderContextInjectionEnumContextResolver.class, ProviderContextInjectionTextPlainEnumContextResolver.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ProviderContextInjectionTest.class.getSimpleName());
    }

    @AfterClass
    public static void close() {
        client.close();
    }

    /**
     * @tpTestDetails Providers are injected into Resource with @Context injection. The resource gets ContextResolver
     * provider for user defined enum type EnumProvider and verifies that correct application provider was chosen.
     * @tpPassCrit Correct application provider was chosen
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void isRegisteredWildCardContextResolverTest() {
        Response response = client.target(generateURL("/resource/isRegisteredContextResolver")).request().get();
        Assert.assertEquals(200, response.getStatus());
        response.close();
    }


    /**
     * @tpTestDetails Providers are injected into Resource with @Context injection. The resource gets ExceptionMapper
     * provider for RuntimeException and verifies that the correct application provider was chosen.
     * @tpPassCrit Correct application provider was chosen
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testExceptionMapped() {
        Response response = client.target(generateURL("/resource/isRegisteredRuntimeExceptionMapper")).request().get();
        Assert.assertEquals(200, response.getStatus());
        response.close();
    }


}
