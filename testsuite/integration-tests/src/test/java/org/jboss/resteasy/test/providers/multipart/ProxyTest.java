package org.jboss.resteasy.test.providers.multipart;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.providers.multipart.resource.ProxyApiService;
import org.jboss.resteasy.test.providers.multipart.resource.ProxyAttachment;
import org.jboss.resteasy.test.providers.multipart.resource.ProxyResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter Multipart provider
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test proxy with multipart provider
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ProxyTest {

    private static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ProxyTest.class.getSimpleName());
        war.addClass(ProxyApiService.class);
        war.addClass(ProxyAttachment.class);
        return TestUtil.finishContainerPrepare(war, null, ProxyResource.class);
    }

    private static String generateBaseUrl() {
        return PortProviderUtil.generateBaseUrl(ProxyTest.class.getSimpleName());
    }

    @BeforeClass
    public static void before() throws Exception
    {
        client = new ResteasyClientBuilder().build();
    }

    @AfterClass
    public static void after() throws Exception
    {
        client.close();
    }

    /**
     * @tpTestDetails ProxyAttachment object and string object is in request
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testNewBuilder() {
        ProxyApiService apiService = client.target(generateBaseUrl()).proxy(ProxyApiService.class);
        tryCall(apiService);
    }

    private void tryCall(ProxyApiService apiService) {
        ProxyAttachment attachment = new ProxyAttachment();
        attachment.setData("foo".getBytes());
        apiService.postAttachment(attachment, "some-key"); // any exception in ProxyResource would be thrown from proxy too, no assert needed
    }


}
