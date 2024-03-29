package org.jboss.resteasy.test.providers.multipart;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.test.providers.multipart.resource.InputPartDefaultContentTypeWildcardOverwriteContainerBean;
import org.jboss.resteasy.test.providers.multipart.resource.InputPartDefaultContentTypeWildcardOverwriteNewInterceptor;
import org.jboss.resteasy.test.providers.multipart.resource.InputPartDefaultContentTypeWildcardOverwriteService;
import org.jboss.resteasy.test.providers.multipart.resource.InputPartDefaultContentTypeWildcardOverwriteXmlBean;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Multipart provider
 * @tpChapter Integration tests
 * @tpTestCaseDetails MultiPart provider should be able to process xml, if wildcard is set. Wildcard is set in new version of
 *                    interceptor.
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class InputPartDefaultContentTypeWildcardOverwriteNewInterceptorTest {

    public static final String WILDCARD_WITH_CHARSET_UTF_8 = MediaType.APPLICATION_XML + "; charset=UTF-8"; // this mediatype works correctly
    private static Client client;

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil
                .prepareArchive(InputPartDefaultContentTypeWildcardOverwriteNewInterceptorTest.class.getSimpleName());
        war.addClasses(InputPartDefaultContentTypeWildcardOverwriteContainerBean.class);
        war.addClasses(InputPartDefaultContentTypeWildcardOverwriteXmlBean.class,
                InputPartDefaultContentTypeWildcardOverwriteNewInterceptorTest.class);
        return TestUtil.finishContainerPrepare(war, null, InputPartDefaultContentTypeWildcardOverwriteNewInterceptor.class,
                InputPartDefaultContentTypeWildcardOverwriteService.class);
    }

    @BeforeAll
    public static void before() throws Exception {
        client = ClientBuilder.newClient();
    }

    @AfterAll
    public static void after() throws Exception {
        client.close();
    }

    /**
     * @tpTestDetails Test for new client
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testContentTypeNewClient() throws Exception {
        String message = "--boo\r\n"
                + "Content-Disposition: form-data; name=\"foo\"\r\n"
                + "Content-Transfer-Encoding: 8bit\r\n\r\n"
                + "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<inputPartDefaultContentTypeWildcardOverwriteXmlBean><myInt>27</myInt><myString>Lorem Ipsum</myString></inputPartDefaultContentTypeWildcardOverwriteXmlBean>\r\n"
                + "--boo--\r\n";

        WebTarget target = client.target(PortProviderUtil.generateURL("/mime",
                InputPartDefaultContentTypeWildcardOverwriteNewInterceptorTest.class.getSimpleName()));
        Entity entity = Entity.entity(message, "multipart/form-data; boundary=boo");
        Response response = target.request().post(entity);

        Assertions.assertEquals(20, response.getStatus() / 10,
                "MultiPart provider is unable to process xml, if media type is set in interceptor");
        Assertions.assertEquals("27", response.readEntity(String.class), "Response text is wrong");
    }
}
