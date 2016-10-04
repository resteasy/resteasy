package org.jboss.resteasy.test.client;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ProxyBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.client.resource.ClientFormResource;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Client tests
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ClientFormParamTest extends ClientTestBase{

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

    @Before
    public void init() {
        client = new ResteasyClientBuilder().build();
    }

    @After
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
        Assert.assertEquals("The result doesn't match the expected one", result, "value");
        result = client.target(generateURL("/form")).request().post(Entity.form(new Form().param("value", "value")),
                String.class);
        Assert.assertEquals("The result doesn't match the expected on, when using Form parameter", result, "value");
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
        Assert.assertEquals("The form map size on the response doesn't match the original", resultForm.asMap().size(),
                form.asMap().size());
        Assert.assertEquals("The resulting form doesn't contain the value for 'bill'",
                resultForm.asMap().getFirst("bill"), "burke");
        Assert.assertEquals("The resulting form doesn't contain the value for 'foo'", resultForm.asMap()
                .getFirst("foo"), "bar");
    }

}
