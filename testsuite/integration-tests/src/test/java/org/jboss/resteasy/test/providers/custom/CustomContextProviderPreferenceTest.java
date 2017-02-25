package org.jboss.resteasy.test.providers.custom;

import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.providers.custom.resource.CustomContextProviderPreferenceResolver;
import org.jboss.resteasy.test.providers.custom.resource.CustomContextProviderPreferenceResource;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter Providers
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for custom ContextProvider preference.
 * @tpSince RESTEasy 3.1.2.Final
 */
@RunWith(Arquillian.class)
@RunAsClient
public class CustomContextProviderPreferenceTest {

    static ResteasyClient client;

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(CustomContextProviderPreferenceTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, CustomContextProviderPreferenceResolver.class,
              CustomContextProviderPreferenceResource.class);
    }

    @Before
    public void init() {
        client = new ResteasyClientBuilder().build();
    }

    @After
    public void after() throws Exception {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, CustomContextProviderPreferenceTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Client test: RESTEASY-1609
     * @tpSince RESTEasy 3.1.2.Final
     */
    @Test
    public void testCustomContextProviderPreference() throws Exception {
        Response response = client.target(generateURL("/test")).request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        response.close();
    }
}
