package org.jboss.resteasy.test.providers.multipart;

import java.io.InputStream;
import java.lang.reflect.ReflectPermission;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartInput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartInputImpl;
import org.jboss.resteasy.plugins.providers.multipart.MultipartRelatedInput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartRelatedOutput;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.providers.multipart.resource.ComplexMultipartOutputResource;
import org.jboss.resteasy.utils.PermissionUtil;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ComplexMultipartOutputTest {
    protected final Logger logger = Logger.getLogger(
            ComplexMultipartOutputTest.class.getName());

    static ResteasyClient client;

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(ComplexMultipartOutputTest.class.getSimpleName());
        war.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
                new ReflectPermission("suppressAccessChecks")), "permissions.xml");
        return TestUtil.finishContainerPrepare(war, null,
                ComplexMultipartOutputResource.class);
    }

    @BeforeAll
    public static void before() throws Exception {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterAll
    public static void after() throws Exception {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path,
                ComplexMultipartOutputTest.class.getSimpleName());
    }

    @Test
    public void testGetComplexCase() throws Exception {

        List<String> controlList = new ArrayList<>();
        controlList.add("bill");
        controlList.add("bob");

        ResteasyWebTarget target = client.target(generateURL("/mpart/test"));
        Response response = target.request().get();
        MultipartInput multipartInput = response.readEntity(MultipartInput.class);
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());

        List<InputPart> parts = multipartInput.getParts(); // debug
        Assertions.assertEquals(2, parts.size());

        for (InputPart inputPart : multipartInput.getParts()) {

            MultipartRelatedInput mRelatedInput = inputPart.getBody(
                    MultipartRelatedInput.class, null);
            Assertions.assertEquals(1, mRelatedInput.getRelatedMap().size());

            for (Map.Entry<String, InputPart> entry : mRelatedInput
                    .getRelatedMap().entrySet()) {
                String key = entry.getKey();
                InputPart iPart = entry.getValue();

                if (controlList.contains(key)) {
                    controlList.remove(key);
                }

                if (iPart instanceof MultipartInputImpl.PartImpl) {
                    MultipartInputImpl.PartImpl miPart = (MultipartInputImpl.PartImpl) iPart;
                    InputStream inStream = miPart.getBody();
                    Assertions.assertNotNull(
                            inStream, "InputStream should not be null.");
                }
            }

        }

        if (!controlList.isEmpty()) {
            Assertions.fail("1 or more missing MultipartRelatedInput return objects");
        }
    }

    @Test
    public void testPostComplexCase() throws Exception {
        MultipartRelatedOutput mRelatedOutput = new MultipartRelatedOutput();
        mRelatedOutput.setStartInfo("text/html");
        mRelatedOutput.addPart("Bill", new MediaType("image",
                "png"), "bill", "binary");
        mRelatedOutput.addPart("Bob", new MediaType("image",
                "png"), "bob", "binary");

        WebTarget target = client.target(generateURL("/mpart/post/related"));
        Entity<MultipartRelatedOutput> entity = Entity.entity(mRelatedOutput,
                new MediaType("multipart", "related"));
        Response response = target.request().post(entity);
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());

        MultipartRelatedInput result = response.readEntity(MultipartRelatedInput.class);
        Set<String> keys = result.getRelatedMap().keySet();
        Assertions.assertTrue(keys.size() == 2);
        Assertions.assertTrue(keys.contains("Bill"), "Failed to find inputPart Bill");
        Assertions.assertTrue(keys.contains("Bob"), "Failed to find inputPart Bob");
    }
}
