package org.jboss.resteasy.test.form;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.hamcrest.MatcherAssert;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.test.form.resource.FormContainerRequestFilterResource;
import org.jboss.resteasy.test.form.resource.FormContainerRequestFilterFilter;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.core.IsNull.notNullValue;

/**
 * @tpSubChapter Form tests
 * @tpChapter Integration tests
 * @tpSince RESTEasy 6.2.2.Final
 * (RESTEASY-567) Verify that PUT and POST endpoints with preceding ContainerRequestFilter
 * pass the FormParam data.
 */
@RunWith(Arquillian.class)
@RunAsClient
public class FormContainerRequestFilterTest {

    static ResteasyClient client;

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(FormContainerRequestFilterTest.class.getSimpleName());
        war.addClasses(FormContainerRequestFilterTest.class);
        return TestUtil.finishContainerPrepare(war, null,
                FormContainerRequestFilterResource.class,
                FormContainerRequestFilterFilter.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, FormContainerRequestFilterTest.class.getSimpleName());
    }

    @Before
    public void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @After
    public void after() throws Exception {
        client.close();
        client = null;
    }

    @Test
    public void testParamPost() throws Exception {
        Form form = new Form();
        form.param("fp", "abc xyz");
        Response response = client.target(generateURL("/a")).request()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

        MatcherAssert.assertThat("Wrong response", response, notNullValue());
        response.bufferEntity();
        Assert.assertEquals("Wrong response", "abc xyz", response.readEntity(String.class));
    }
    @Test
    public void testParamPut() throws Exception {
        Form form = new Form();
        form.param("fp", "abc xyz");
        Response response = client.target(generateURL("/b")).request()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .put(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

        MatcherAssert.assertThat("Wrong response", response, notNullValue());
        response.bufferEntity();
        Assert.assertEquals("Wrong response", "abc xyz", response.readEntity(String.class));
    }
}
