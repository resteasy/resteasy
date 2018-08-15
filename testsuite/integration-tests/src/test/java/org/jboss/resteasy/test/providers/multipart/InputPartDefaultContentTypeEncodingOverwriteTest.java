package org.jboss.resteasy.test.providers.multipart;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.providers.multipart.resource.InputPartDefaultContentTypeEncodingOverwriteService;
import org.jboss.resteasy.test.providers.multipart.resource.InputPartDefaultContentTypeEncodingOverwriteSetterContainerRequestFilter;
import org.jboss.resteasy.utils.PermissionUtil;
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
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.reflect.ReflectPermission;

/**
 * @tpSubChapter Multipart provider
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for default content type encoding of multipart provider
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class InputPartDefaultContentTypeEncodingOverwriteTest {
    public static final String TEXT_PLAIN_WITH_CHARSET_UTF_8 = "text/plain; charset=utf-8";
    private static Client client;

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(InputPartDefaultContentTypeEncodingOverwriteTest.class.getSimpleName());
        war.addClasses(InputPartDefaultContentTypeEncodingOverwriteTest.class);
        war.addClasses(TestUtil.class, PortProviderUtil.class);
        war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
                new ReflectPermission("suppressAccessChecks")
        ), "permissions.xml");
        return TestUtil.finishContainerPrepare(war, null, InputPartDefaultContentTypeEncodingOverwriteSetterContainerRequestFilter.class,
                InputPartDefaultContentTypeEncodingOverwriteService.class);
    }

    private static String generateURL(String path) {
        return PortProviderUtil.generateURL(path, InputPartDefaultContentTypeEncodingOverwriteTest.class.getSimpleName());
    }

    @BeforeClass
    public static void before() throws Exception
    {
        client = ClientBuilder.newClient();
    }

    @AfterClass
    public static void after() throws Exception
    {
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

        Assert.assertEquals("Status code is wrong.", 20, response.getStatus() / 10);
        Assert.assertEquals("Response text is wrong",
                MediaType.valueOf(TEXT_PLAIN_WITH_CHARSET_UTF_8),
                MediaType.valueOf(response.readEntity(String.class)));
    }
}
