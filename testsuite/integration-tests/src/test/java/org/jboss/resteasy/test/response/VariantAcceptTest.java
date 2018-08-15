package org.jboss.resteasy.test.response;

import static junit.framework.TestCase.assertEquals;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.response.resource.VariantAcceptResource;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-994
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class VariantAcceptTest {

    public static final MediaType WILDCARD_WITH_PARAMS;
    public static final MediaType TEXT_HTML_WITH_PARAMS;
    public static final MediaType TEXT_PLAIN_WITH_PARAMS;

    static {
        Map<String, String> params = new HashMap<String, String>();
        params.put("q", "0.5");
        params.put("a", "1");
        params.put("b", "2");
        params.put("c", "3");
        WILDCARD_WITH_PARAMS = new MediaType("*", "*", params);

        params.clear();
        params.put("a", "1");
        params.put("b", "2");
        params.put("c", "3");
        TEXT_HTML_WITH_PARAMS = new MediaType("text", "html", params);

        params.clear();
        params.put("a", "1");
        params.put("b", "2");
        params.put("c", "3");
        TEXT_PLAIN_WITH_PARAMS = new MediaType("text", "plain", params);
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(VariantAcceptTest.class.getSimpleName());
        war.addClass(VariantAcceptTest.class);
        return TestUtil.finishContainerPrepare(war, null, VariantAcceptResource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, VariantAcceptTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Verifies that a more specific media type is preferred.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testVariant() throws Exception {
        ResteasyClient client = new ResteasyClientBuilder().build();
        Invocation.Builder request = client.target(generateURL("/variant")).request();
        request.accept(MediaType.WILDCARD_TYPE);
        request.accept(MediaType.TEXT_HTML_TYPE);
        Response response = request.get();
        assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String entity = response.readEntity(String.class);
        assertEquals("Wrong media type on response", MediaType.TEXT_HTML, entity);
    }

    /**
     * @tpTestDetails Verifies that the number of parameters does not outweigh more specific media types.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testVariantWithParameters() throws Exception {
        ResteasyClient client = new ResteasyClientBuilder().build();
        Invocation.Builder request = client.target(generateURL("/params")).request();
        request.accept(WILDCARD_WITH_PARAMS);
        request.accept(MediaType.TEXT_HTML_TYPE);
        Response response = request.get();
        assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        String entity = response.readEntity(String.class);
        assertEquals("Wrong media type on response", TEXT_HTML_WITH_PARAMS.toString(), entity);
    }

    /**
     * @tpTestDetails Verifies that the q/qs factors are stripped from the response Content-type header if they are provided
     * in the request/@Produces. See RESTEASY-1765.
     * @tpSince RESTEasy 3.0.25
     */
    @Test
    public void testVariantWithQParameter() throws Exception {
        ResteasyClient client = new ResteasyClientBuilder().build();
        Invocation.Builder request = client.target(generateURL("/simple")).request();
        request.accept("application/json;q=0.3, application/xml;q=0.2");
        Response response = request.get();
        assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        assertEquals("application/json", response.getHeaderString("Content-Type"));

        request = client.target(generateURL("/simpleqs")).request();
        response = request.get();
        assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        assertEquals("application/xml;charset=UTF-8", response.getHeaderString("Content-Type"));
    }
}
