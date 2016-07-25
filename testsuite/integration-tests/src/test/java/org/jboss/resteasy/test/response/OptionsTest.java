package org.jboss.resteasy.test.response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.response.resource.OptionParamsResource;
import org.jboss.resteasy.test.response.resource.OptionUsersResource;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;


/**
 * @tpSubChapter Response
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-363
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class OptionsTest {

    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploySimpleResource() {
        WebArchive war = TestUtil.prepareArchive(OptionsTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, OptionParamsResource.class, OptionUsersResource.class);
    }

    @BeforeClass
    public static void init() {
        client = new ResteasyClientBuilder().build();
    }

    @AfterClass
    public static void close() {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, OptionsTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Check options HTTP request
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testOptions() throws Exception {
        WebTarget base = client.target(generateURL("/params/customers/333/phonenumbers"));
        Response response = base.request().options();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        response.close();
    }

    /**
     * @tpTestDetails Check not allowed request
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMethodNotAllowed() throws Exception {
        WebTarget base = client.target(generateURL("/params/customers/333/phonenumbers"));
        Response response = base.request().post(Entity.text(new String()));
        Assert.assertEquals(HttpResponseCodes.SC_METHOD_NOT_ALLOWED, response.getStatus());
        response.close();

        base = client.target(generateURL("/users"));
        response = base.request().delete();
        Assert.assertEquals(HttpResponseCodes.SC_METHOD_NOT_ALLOWED, response.getStatus());
        response.close();

        base = client.target(generateURL("/users/53"));
        response = base.request().post(Entity.text(new String()));
        Assert.assertEquals(HttpResponseCodes.SC_METHOD_NOT_ALLOWED, response.getStatus());
        response.close();

        base = client.target(generateURL("/users/53/contacts"));
        response = base.request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        response.close();

        base = client.target(generateURL("/users/53/contacts"));
        response = base.request().delete();
        Assert.assertEquals(HttpResponseCodes.SC_METHOD_NOT_ALLOWED, response.getStatus());
        response.close();

        base = client.target(generateURL("/users/53/contacts/carl"));
        response = base.request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        response.close();

        base = client.target(generateURL("/users/53/contacts/carl"));
        response = base.request().post(Entity.text(new String()));
        Assert.assertEquals(HttpResponseCodes.SC_METHOD_NOT_ALLOWED, response.getStatus());
        response.close();
    }

}
