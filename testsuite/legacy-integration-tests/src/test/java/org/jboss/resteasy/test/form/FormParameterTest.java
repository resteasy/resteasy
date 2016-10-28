package org.jboss.resteasy.test.form;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.form.resource.FormParameterResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @tpSubChapter Form tests
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-760
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class FormParameterTest {

    static ResteasyClient client;

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(FormParameterTest.class.getSimpleName());
        war.addClasses(FormParameterTest.class);
        return TestUtil.finishContainerPrepare(war, null, FormParameterResource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, FormParameterTest.class.getSimpleName());
    }

    @Before
    public void init() {
        client = new ResteasyClientBuilder().build();
    }

    @After
    public void after() throws Exception {
        client.close();
        client = null;
    }

    /**
     * @tpTestDetails Client sends PUT requests.
     *      Form parameter is used and should be returned by RE resource.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testFormParamWithNoQueryParamPut() throws Exception {
        Form form = new Form();
        form.param("formParam", "abc xyz");
        Response response = client.target(generateURL("/put/noquery/")).request()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .put(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
                assertThat("Wrong response", response, notNullValue());

        response.bufferEntity();
        assertEquals("Wrong response", "abc xyz", response.readEntity(String.class));
    }

    /**
     * @tpTestDetails Client sends PUT requests.
     *      Form parameter is used (encoded) and should be returned by RE resource.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testFormParamWithNoQueryParamPutEncoded() throws Exception {
        Form form = new Form();
        form.param("formParam", "abc xyz");
        Response response = client.target(generateURL("/put/noquery/encoded")).request()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .put(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

        assertThat("Wrong response", response, notNullValue());
        response.bufferEntity();
        assertEquals("Wrong response", "abc+xyz", response.readEntity(String.class));
    }

    /**
     * @tpTestDetails Client sends POST requests.
     *      Form parameter is used and should be returned by RE resource.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testFormParamWithNoQueryParamPost() throws Exception {
        Form form = new Form();
        form.param("formParam", "abc xyz");
        Response response = client.target(generateURL("/post/noquery/")).request()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

        assertThat("Wrong response", response, notNullValue());
        response.bufferEntity();
        assertEquals("Wrong response", "abc xyz", response.readEntity(String.class));
    }

    /**
     * @tpTestDetails Client sends POST requests.
     *      Form parameter is used (encoded) and should be returned by RE resource.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testFormParamWithNoQueryParamPostEncoded() throws Exception {
        Form form = new Form();
        form.param("formParam", "abc xyz");
        Response response = client.target(generateURL("/post/noquery/encoded")).request()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

        assertThat("Wrong response", response, notNullValue());
        response.bufferEntity();
        assertEquals("Wrong response", "abc+xyz", response.readEntity(String.class));
    }

    /**
     * @tpTestDetails Client sends PUT requests. Query parameter is used.
     *      Form parameter is used too and should be returned by RE resource.
     *      This is regression test for JBEAP-982
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testFormParamWithQueryParamPut() throws Exception {
        Form form = new Form();
        form.param("formParam", "abc xyz");
        Response response = client.target(generateURL("/put/query?query=xyz")).request()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .put(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

        assertThat("Wrong response", response, notNullValue());
        response.bufferEntity();
        assertEquals("Wrong response", "abc xyz", response.readEntity(String.class));
    }

    /**
     * @tpTestDetails Client sends PUT requests. Query parameter is used.
     *      Form parameter is used too (encoded) and should be returned by RE resource.
     *      This is regression test for JBEAP-982
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testFormParamWithQueryParamPutEncoded() throws Exception {
        Form form = new Form();
        form.param("formParam", "abc xyz");
        Response response = client.target(generateURL("/put/query/encoded?query=xyz")).request()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .put(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

        assertThat("Wrong response", response, notNullValue());
        response.bufferEntity();
        assertEquals("Wrong response", "abc+xyz", response.readEntity(String.class));
    }

    /**
     * @tpTestDetails Client sends POST requests. Query parameter is used.
     *      Form parameter is used too and should be returned by RE resource.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testFormParamWithQueryParamPost() throws Exception {
        Form form = new Form();
        form.param("formParam", "abc xyz");
        Response response = client.target(generateURL("/post/query?query=xyz")).request()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

        assertThat("Wrong response", response, notNullValue());
        response.bufferEntity();
        assertEquals("Wrong response", "abc xyz", response.readEntity(String.class));
    }

    /**
     * @tpTestDetails Client sends POST requests. Query parameter is used.
     *      Form parameter is used too (encoded) and should be returned by RE resource.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testFormParamWithQueryParamPostEncoded() throws Exception {
        Form form = new Form();
        form.param("formParam", "abc xyz");
        Response response = client.target(generateURL("/post/query/encoded?query=xyz")).request()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

        assertThat("Wrong response", response, notNullValue());
        response.bufferEntity();
        assertEquals("Wrong response", "abc+xyz", response.readEntity(String.class));
    }
}
