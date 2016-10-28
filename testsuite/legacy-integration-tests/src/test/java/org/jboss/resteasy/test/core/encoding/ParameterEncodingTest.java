package org.jboss.resteasy.test.core.encoding;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.test.core.encoding.resource.ParameterEncodingResource;
import org.jboss.resteasy.util.HttpResponseCodes;
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
import javax.ws.rs.core.Response;

import static junit.framework.TestCase.assertEquals;

/**
 * @tpSubChapter Encoding
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-737
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ParameterEncodingTest {

    protected ResteasyClient client;

    @Before
    public void setup() throws Exception {
        client = new ResteasyClientBuilder().build();
    }


    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ParameterEncodingTest.class.getSimpleName());
    }

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(ParameterEncodingTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, ParameterEncodingResource.class);
    }

    @After
    public void shutdown() throws Exception {
        client.close();
    }

    /**
     * @tpTestDetails Check space encoding in URL
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testResteasy734() throws Exception {
        ResteasyWebTarget target = null;
        Response response = null;

        target = client.target(generateURL("/encoded/pathparam/bee bop"));
        response = target.request().get();
        String entity = response.readEntity(String.class);
        assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        assertEquals("bee%20bop", entity);
        response.close();

        target = client.target(generateURL("/decoded/pathparam/bee bop"));
        response = target.request().get();
        entity = response.readEntity(String.class);
        assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        assertEquals("bee bop", entity);
        response.close();

        target = client.target(generateURL("/encoded/matrix;m=bee bop"));
        response = target.request().get();
        entity = response.readEntity(String.class);
        assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        assertEquals("bee%20bop", entity);
        response.close();

        target = client.target(generateURL("/decoded/matrix;m=bee bop"));
        response = target.request().get();
        entity = response.readEntity(String.class);
        assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        assertEquals("bee bop", entity);
        response.close();

        target = client.target(generateURL("/encoded/query?m=bee bop"));
        response = target.request().get();
        entity = response.readEntity(String.class);
        assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        assertEquals("bee%20bop", entity);
        response.close();

        target = client.target(generateURL("/decoded/query?m=bee bop"));
        response = target.request().get();
        entity = response.readEntity(String.class);
        assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        assertEquals("bee bop", entity);
        response.close();

        target = client.target(generateURL("/encoded/form"));
        Form form = new Form();
        form.param("f", "bee bop");
        response = target.request().post(Entity.form(form));
        entity = response.readEntity(String.class);
        assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        assertEquals("bee+bop", entity);
        response.close();

        target = client.target(generateURL("/decoded/form"));
        form = new Form();
        form.param("f", "bee bop");
        response = target.request().post(Entity.form(form));
        entity = response.readEntity(String.class);
        assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        assertEquals("bee bop", entity);
        response.close();

        target = client.target(generateURL("/encoded/segment/bee bop"));
        response = target.request().get();
        entity = response.readEntity(String.class);
        assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        assertEquals("bee%20bop", entity);
        response.close();

        target = client.target(generateURL("/decoded/segment/bee bop"));
        response = target.request().get();
        entity = response.readEntity(String.class);
        assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        assertEquals("bee bop", entity);
        response.close();

        target = client.target(generateURL("/encoded/segment/matrix/params;m=bee bop"));
        response = target.request().get();
        entity = response.readEntity(String.class);
        assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        assertEquals("bee%20bop", entity);
        response.close();

        target = client.target(generateURL("/decoded/segment/matrix/params;m=bee bop"));
        response = target.request().get();
        entity = response.readEntity(String.class);
        assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        assertEquals("bee bop", entity);
        response.close();
    }
}
