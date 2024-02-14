package org.jboss.resteasy.test.providers.multipart;

import java.lang.reflect.ReflectPermission;
import java.util.Iterator;
import java.util.List;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
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
import org.jboss.resteasy.plugins.providers.multipart.MultipartOutput;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.providers.multipart.resource.Soup;
import org.jboss.resteasy.test.providers.multipart.resource.SoupVendorResource;
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
public class SoupMultipartMsgTest {
    protected final Logger logger = Logger.getLogger(SoupMultipartMsgTest.class.getName());
    static ResteasyClient client;

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(SoupMultipartMsgTest.class.getSimpleName());
        war.addClasses(Soup.class);
        war.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        war.addAsManifestResource(PermissionUtil.createPermissionsXmlAsset(
                new ReflectPermission("suppressAccessChecks")), "permissions.xml");
        return TestUtil.finishContainerPrepare(war, null, SoupVendorResource.class);
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
        return PortProviderUtil.generateURL(path, SoupMultipartMsgTest.class.getSimpleName());
    }

    @Test
    public void testPostMsg() throws Exception {

        MultipartOutput multipartOutput = new MultipartOutput();
        multipartOutput.addPart(new Soup("Chicken Noodle"),
                MediaType.APPLICATION_XML_TYPE);
        multipartOutput.addPart(new Soup("Vegetable"),
                MediaType.APPLICATION_XML_TYPE);
        multipartOutput.addPart("Granny's Soups", MediaType.TEXT_PLAIN_TYPE);

        ResteasyWebTarget target = client.target(generateURL("/vendor/register/soups"));
        Entity<MultipartOutput> entity = Entity.entity(multipartOutput,
                new MediaType("multipart", "mixed"));
        Response response = target.request().post(entity);

        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String result = response.readEntity(String.class);

        if (result.startsWith("Failed")) {
            Assertions.fail(result);
        }
    }

    @Test
    public void testSoupObj() throws Exception {
        getMessage("/vendor/soups/obj");
    }

    @Test
    public void testSoupResp() throws Exception {
        getMessage("/vendor/soups/resp");
    }

    private void getMessage(String path) throws Exception {
        ResteasyWebTarget target = client.target(generateURL(path));
        Response response = target.request().get();

        MultipartInput multipartInput = response.readEntity(MultipartInput.class);
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());

        List<String> controlList = SoupVendorResource.getControlList();

        for (InputPart inputPart : multipartInput.getParts()) {
            if (MediaType.APPLICATION_XML_TYPE.equals(inputPart.getMediaType())) {
                Soup c = inputPart.getBody(Soup.class, null);
                String name = c.getId();
                if (controlList.contains(name)) {
                    controlList.remove(name);
                }
            } else {
                String name = inputPart.getBody(String.class, null);
                if (controlList.contains(name)) {
                    controlList.remove(name);
                }
            }
        }

        // verify content and report to test
        StringBuilder sb = new StringBuilder();
        if (!controlList.isEmpty()) {
            sb.append("Failed: parts not found: ");
            for (Iterator<String> it = controlList.iterator(); it.hasNext();) {
                sb.append(it.next() + " ");
            }
            Assertions.fail(sb.toString());
        }

    }

    @Test
    public void testSouplistObj() throws Exception {
        getGenericTypeMessage("/vendor/souplist/obj");
    }

    @Test
    public void testSouplistResp() throws Exception {
        getGenericTypeMessage("/vendor/souplist/resp");
    }

    private void getGenericTypeMessage(String path) throws Exception {
        ResteasyWebTarget target = client.target(generateURL(path));
        Response response = target.request().get();
        MultipartInput multipartInput = response.readEntity(MultipartInput.class);

        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        List<String> controlList = SoupVendorResource.getControlList();

        GenericType<List<Soup>> gType = new GenericType<List<Soup>>() {
        };
        for (InputPart inputPart : multipartInput.getParts()) {
            if (MediaType.APPLICATION_XML_TYPE.equals(inputPart.getMediaType())) {
                // List<Soup> soupList = inputPart.getBody(gType.getRawType(), gType.getType());
                List<Soup> soupList = inputPart.getBody(gType);
                for (Soup soup : soupList) {
                    String name = soup.getId();
                    if (controlList.contains(name)) {
                        controlList.remove(name);
                    }
                }

            } else {
                String name = inputPart.getBody(String.class, null);
                if (controlList.contains(name)) {
                    controlList.remove(name);
                }
            }
        }

        // verify content and report to test
        StringBuilder sb = new StringBuilder();
        if (!controlList.isEmpty()) {
            sb.append("Failed: parts not found: ");
            for (Iterator<String> it = controlList.iterator(); it.hasNext();) {
                sb.append(it.next() + " ");
            }
            Assertions.fail(sb.toString());
        }

    }

    @Test
    public void testSoupFile() throws Exception {

        ResteasyWebTarget target = client.target(generateURL("/vendor/soupfile"));
        Response response = target.request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());

        MultipartInput multipartInput = response.readEntity(MultipartInput.class);
        InputPart inputPart = multipartInput.getParts().get(0);
        String bodyStr = inputPart.getBodyAsString();
        Assertions.assertTrue(bodyStr.contains("Vegetable"),
                "Failed to return expected data.");
    }
}
