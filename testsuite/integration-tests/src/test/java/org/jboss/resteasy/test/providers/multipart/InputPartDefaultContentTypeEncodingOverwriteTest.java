package org.jboss.resteasy.test.providers.multipart;

import java.lang.reflect.ReflectPermission;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.test.providers.multipart.resource.InputPartDefaultContentTypeEncodingOverwriteService;
import org.jboss.resteasy.test.providers.multipart.resource.InputPartDefaultContentTypeEncodingOverwriteSetterContainerRequestFilter;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.wildfly.testing.tools.deployments.DeploymentDescriptors;

/**
 * @tpSubChapter Multipart provider
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for default content type encoding of multipart provider
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class InputPartDefaultContentTypeEncodingOverwriteTest {
    public static final String TEXT_PLAIN_WITH_CHARSET_UTF_8 = "text/plain; charset=utf-8";
    private static Client client;

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(InputPartDefaultContentTypeEncodingOverwriteTest.class.getSimpleName());
        war.addClasses(InputPartDefaultContentTypeEncodingOverwriteTest.class);
        war.addClasses(TestUtil.class, PortProviderUtil.class);
        war.addAsManifestResource(DeploymentDescriptors.createPermissionsXmlAsset(
                new ReflectPermission("suppressAccessChecks")), "permissions.xml");
        return TestUtil.finishContainerPrepare(war, null,
                InputPartDefaultContentTypeEncodingOverwriteSetterContainerRequestFilter.class,
                InputPartDefaultContentTypeEncodingOverwriteService.class);
    }

    private static String generateURL(String path) {
        return PortProviderUtil.generateURL(path, InputPartDefaultContentTypeEncodingOverwriteTest.class.getSimpleName());
    }

    @BeforeAll
    public static void before() throws Exception {
        client = ClientBuilder.newClient();
    }

    @AfterAll
    public static void after() throws Exception {
        client.close();
    }

    private static final String TEST_URI = generateURL("");

    /**
     * @tpTestDetails Test for new client
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testContentTypeNewClient() throws Exception {
        String message = "--boo\r\n"
                + "Content-Disposition: form-data; name=\"foo\"\r\n"
                + "Content-Transfer-Encoding: 8bit\r\n\r\n" + "bar\r\n"
                + "--boo--\r\n";

        WebTarget target = client.target(generateURL("/mime"));
        Entity entity = Entity.entity(message, "multipart/form-data; boundary=boo");
        Response response = target.request().post(entity);

        Assertions.assertEquals(20, response.getStatus() / 10, "Status code is wrong.");
        Assertions.assertEquals(MediaType.valueOf(TEXT_PLAIN_WITH_CHARSET_UTF_8),
                MediaType.valueOf(response.readEntity(String.class)), "Response text is wrong");
    }
}
