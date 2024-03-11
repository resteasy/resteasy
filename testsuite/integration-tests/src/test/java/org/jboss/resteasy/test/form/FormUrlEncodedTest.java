package org.jboss.resteasy.test.form;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation.Builder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.specimpl.MultivaluedMapImpl;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.form.resource.FormUrlEncodedResource;
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
 * @tpSubChapter Form tests
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class FormUrlEncodedTest {

    private static Client client;

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(FormUrlEncodedTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, FormUrlEncodedResource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, FormUrlEncodedTest.class.getSimpleName());
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
     * @tpTestDetails Get form parameter from resource using InputStream and StreamingOutput
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testPost() {
        WebTarget base = client.target(generateURL("/simple"));
        Response response = base.request().post(Entity.form(new Form().param("hello", "world")));

        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String body = response.readEntity(String.class);
        Assertions.assertEquals("hello=world", body, "Wrong response content");

        response.close();
    }

    /**
     * @tpTestDetails Send form with an empty parameter value.
     * @tpSince RESTEasy 3.0.20
     */
    @Test
    public void testResteasy109() {
        Builder builder = client.target(generateURL("/RESTEASY-109")).request();
        Response response = null;
        try {
            response = builder.post(
                    Entity.entity("name=jon&address1=123+Main+St&address2=&zip=12345", MediaType.APPLICATION_FORM_URLENCODED));
            Assertions.assertEquals(204, response.getStatus());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            response.close();
        }
    }

    /**
     * @tpTestDetails Send form with a missing query parameter.
     * @tpSince RESTEasy 3.0.20
     */
    @Test
    public void testQueryParamIsNull() {
        Builder builder = client.target(generateURL("/simple")).request();
        try {
            Response response = builder.post(Entity.form(new Form("hello", "world")));
            Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            Assertions.assertEquals("hello=world", response.readEntity(String.class));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @tpTestDetails Send form with two parameters.
     * @tpSince RESTEasy 3.0.20
     */
    @Test
    public void testPostTwoParameters() {
        Builder builder = client.target(generateURL("/form/twoparams")).request();
        try {
            Response response = builder.post(Entity.form(new Form("hello", "world").param("yo", "mama")));
            Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
            String body = response.readEntity(String.class);
            Assertions.assertTrue(body.indexOf("hello=world") != -1);
            Assertions.assertTrue(body.indexOf("yo=mama") != -1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Path("/")
    public interface TestProxy {
        @Path("/form")
        @POST
        @Produces("application/x-www-form-urlencoded")
        @Consumes("application/x-www-form-urlencoded")
        String post(MultivaluedMap<String, String> form);

        @Path("/form")
        @POST
        @Produces("application/x-www-form-urlencoded")
        @Consumes("application/x-www-form-urlencoded")
        MultivaluedMap<String, String> post2(MultivaluedMap<String, String> form);
    }

    /**
     * @tpTestDetails Send form by proxy.
     * @tpSince RESTEasy 3.0.20
     */
    @Test
    public void testProxy() {
        ResteasyWebTarget target = (ResteasyWebTarget) client.target(generateURL(""));
        TestProxy proxy = target.proxy(TestProxy.class);
        MultivaluedMapImpl<String, String> form = new MultivaluedMapImpl<String, String>();
        form.add("hello", "world");
        String body = proxy.post(form);
        Assertions.assertEquals("hello=world", body);

        MultivaluedMap<String, String> rtn = proxy.post2(form);
        Assertions.assertEquals(rtn.getFirst("hello"), "world");
    }
}
