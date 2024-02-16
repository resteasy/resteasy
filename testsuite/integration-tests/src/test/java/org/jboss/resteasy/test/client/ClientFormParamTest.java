package org.jboss.resteasy.test.client;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.MediaType;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ProxyBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.test.client.resource.ClientFormResource;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Client tests
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ClientFormParamTest extends ClientTestBase {

    static ResteasyClient client;

    @Path("/form")
    public interface ClientFormResourceInterface {
        @POST
        String put(@FormParam("value") String value);

        @POST
        @Path("object")
        @Produces(MediaType.APPLICATION_FORM_URLENCODED)
        @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
        Form post(Form form);
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ClientFormParamTest.class.getSimpleName());
        war.addClass(ClientFormParamTest.class);
        war.addClass(ClientTestBase.class);
        return TestUtil.finishContainerPrepare(war, null, ClientFormResource.class);
    }

    @BeforeEach
    public void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterEach
    public void after() throws Exception {
        client.close();
    }

    /**
     * @tpTestDetails Client sends POST request with Form entity with one parameter.
     * @tpPassCrit The resulting Form entity contains the original parameter
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testClientFormOneParameter() throws Exception {

        final ClientFormResourceInterface proxy = ProxyBuilder.builder(ClientFormResourceInterface.class,
                client.target(generateURL(""))).build();
        String result = proxy.put("value");
        Assertions.assertEquals(result, "value", "The result doesn't match the expected one");
        result = client.target(generateURL("/form")).request().post(Entity.form(new Form().param("value", "value")),
                String.class);
        Assertions.assertEquals(result, "value",
                "The result doesn't match the expected on, when using Form parameter");
    }

    /**
     * @tpTestDetails Client sends POST request with Form entity with two parameters.
     * @tpPassCrit The resulting Form entity contains both original parameters
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testClientFormTwoParameters() throws Exception {
        final ClientFormResourceInterface proxy = ProxyBuilder.builder(ClientFormResourceInterface.class,
                client.target(generateURL(""))).build();
        Form form = new Form().param("bill", "burke").param("foo", "bar");
        Form resultForm = proxy.post(form);
        Assertions.assertEquals(resultForm.asMap().size(), form.asMap().size(),
                "The form map size on the response doesn't match the original");
        Assertions.assertEquals(resultForm.asMap().getFirst("bill"), "burke",
                "The resulting form doesn't contain the value for 'bill'");
        Assertions.assertEquals(resultForm.asMap().getFirst("foo"), "bar",
                "The resulting form doesn't contain the value for 'foo'");
    }

}
