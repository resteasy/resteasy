package org.jboss.resteasy.test.providers.custom;

import java.lang.reflect.ReflectPermission;
import java.net.SocketPermission;
import java.util.HashMap;
import java.util.Map;
import java.util.PropertyPermission;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.providers.custom.resource.ReaderWriterCustomer;
import org.jboss.resteasy.test.providers.custom.resource.ReaderWriterResource;
import org.jboss.resteasy.test.providers.custom.resource.WriterNotBuiltinTestWriter;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Providers
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-1
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
public class WriterNotBuiltinTest {

    static ResteasyClient client;

    @Deployment
    public static Archive<?> deployDefaultTestPlain() {
        WebArchive war = TestUtil.prepareArchive(WriterNotBuiltinTest.class.getSimpleName());
        war.addClass(ReaderWriterCustomer.class);
        war.addClass(PortProviderUtil.class);
        Map<String, String> contextParams = new HashMap<>();
        contextParams.put("resteasy.use.builtin.providers", "false");
        // Arquillian in the deployment
        war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
                new PropertyPermission("arquillian.*", "read"),
                new PropertyPermission("ipv6", "read"),
                new PropertyPermission("node", "read"),
                new PropertyPermission("org.jboss.resteasy.port", "read"),
                new PropertyPermission("quarkus.tester", "read"),
                new ReflectPermission("suppressAccessChecks"),
                new RuntimePermission("accessDeclaredMembers"),
                new RuntimePermission("getenv.RESTEASY_PORT"),
                new SocketPermission(PortProviderUtil.getHost(), "connect,resolve")), "permissions.xml");
        return TestUtil.finishContainerPrepare(war, contextParams, WriterNotBuiltinTestWriter.class,
                ReaderWriterResource.class);
    }

    /**
     * @tpTestDetails A more complete test for RESTEASY-1.
     *                TestReaderWriter has no type parameter,
     *                so it comes after DefaultPlainText in the built-in ordering.
     *                The fact that TestReaderWriter gets called verifies that
     *                DefaultPlainText gets passed over.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void test1New() throws Exception {
        client = (ResteasyClient) ClientBuilder.newClient();
        Response response = client.target(PortProviderUtil.generateURL("/string", WriterNotBuiltinTest.class.getSimpleName()))
                .request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("text/plain;charset=UTF-8", response.getStringHeaders().getFirst("content-type"));
        Assertions.assertEquals("hello world", response.readEntity(String.class), "Response contains wrong content");
        Assertions.assertTrue(WriterNotBuiltinTestWriter.used, "Wrong MessageBodyWriter was used");
        client.close();
    }
}
