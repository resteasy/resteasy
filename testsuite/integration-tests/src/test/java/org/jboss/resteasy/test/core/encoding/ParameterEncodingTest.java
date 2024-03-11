package org.jboss.resteasy.test.core.encoding;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.core.encoding.resource.ParameterEncodingResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Encoding
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-737
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ParameterEncodingTest {

    protected ResteasyClient client;

    @BeforeEach
    public void setup() throws Exception {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ParameterEncodingTest.class.getSimpleName());
    }

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(ParameterEncodingTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, ParameterEncodingResource.class);
    }

    @AfterEach
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
