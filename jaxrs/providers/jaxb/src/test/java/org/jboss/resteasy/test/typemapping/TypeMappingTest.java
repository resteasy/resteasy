package org.jboss.resteasy.test.typemapping;

import static org.jboss.resteasy.test.TestPortProvider.*;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jboss.resteasy.plugins.server.tjws.TJWSEmbeddedJaxrsServer;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TypeMappingTest {

    private HttpClient hc;

    private TJWSEmbeddedJaxrsServer tjws;

    @Test
    public void acceptXMLOnlyRequestNoProducesNoExtension() throws Exception {
        requestAndAssert("noproduces", null, "application/xml", "application/xml");
    }

    @Test
    public void acceptJSONOnlyRequestNoProducesNoExtension() throws Exception {
        requestAndAssert("noproduces", null, "application/json", "application/json");
    }

    @Test
    public void acceptNullRequestNoProducesJSONExtension() throws Exception {
        requestAndAssert("noproduces", "json", null, "application/json");
    }

    @Test
    public void acceptNullRequestNoProducesXMLExtension() throws Exception {
        requestAndAssert("noproduces", "xml", null, "application/xml");
    }

    @Test
    public void acceptJSONOnlyRequestNoProducesJSONExtension() throws Exception {
        requestAndAssert("noproduces", "json", "application/json", "application/json");
    }

    @Test
    public void acceptJSONOnlyRequestNoProducesXMLExtension() throws Exception {
        requestAndAssert("noproduces", "xml", "application/json", "application/xml");
    }

    @Test
    public void acceptJSONAndXMLRequestNoProducesJSONExtension() throws Exception {
        requestAndAssert("noproduces", "json", "application/json, application/xml",
                "application/json");
    }

    @Test
    public void acceptXMLAndJSONRequestNoProducesJSONExtension() throws Exception {
        requestAndAssert("noproduces", "json", "application/xml, application/json",
                "application/json");
    }

    @Test
    public void acceptXMLOnlyRequestNoProducesXMLExtension() throws Exception {
        requestAndAssert("noproduces", "xml", "application/xml", "application/xml");
    }

    @Test
    public void acceptXMLOnlyRequestNoProducesJSONExtension() throws Exception {
        requestAndAssert("noproduces", "json", "application/xml", "application/json");
    }

    @Test
    public void acceptJSONAndXMLRequestNoProducesXMLExtension() throws Exception {
        requestAndAssert("noproduces", "xml", "application/json, application/xml",
                "application/xml");
    }

    @Test
    public void acceptXMLAndJSONRequestNoProducesXMLExtension() throws Exception {
        requestAndAssert("noproduces", "xml", "application/xml, application/json",
                "application/xml");
    }

    @Before
    public void startServer() {
        hc = new HttpClient();
        tjws = new TJWSEmbeddedJaxrsServer();
        tjws.setPort(getPort());
        tjws.getRegistry().addPerRequestResource(TestResource.class);

        Map<String, MediaType> mediaTypeMappings = new HashMap<String, MediaType>();
        mediaTypeMappings.put("xml", new MediaType("application", "xml"));
        mediaTypeMappings.put("json", new MediaType("application", "json"));

        tjws.getDispatcher().setMediaTypeMappings(mediaTypeMappings);
        tjws.start();

    }

    @After
    public void stopServer() {
        tjws.stop();
    }

    private void requestAndAssert(String path, String extension, String accept,
            String expectedContentType) throws Exception {
        String url = generateURL("/test/" + path);
        if (extension != null) {
            url = url + "." + extension;
        }
        GetMethod gm = new GetMethod(url);
        if (accept != null) {
            gm.setRequestHeader(HttpHeaderNames.ACCEPT, accept);
        }
        int status = hc.executeMethod(gm);
        assertEquals("Request for " + url + " returned a non-200 status", 200, status);
        assertEquals("Request for " + url + " returned an unexpected content type",
                expectedContentType, gm.getResponseHeader("Content-type").getValue());
    }

    @XmlRootElement
    public static class TestBean {
        private String name;

        public TestBean() {

        }

        public TestBean(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

    @Path("/test")
    public static class TestResource {

        @GET
        @Path("/noproduces")
        public TestBean get() {
            return new TestBean("name");
        }
    }
}
