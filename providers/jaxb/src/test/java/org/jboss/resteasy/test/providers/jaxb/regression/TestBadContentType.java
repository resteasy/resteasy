package org.jboss.resteasy.test.providers.jaxb.regression;

import static org.junit.Assert.*;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jboss.resteasy.plugins.server.tjws.TJWSEmbeddedJaxrsServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * Test case for RESTEASY-169
 * 
 * @author edelsonj
 *
 */

public class TestBadContentType {
    
    @Path("/test")
    public static class TestResource {

        @GET
        public TestBean get() {
            TestBean bean = new TestBean();
            bean.setName("myname");
            return bean;
        }

    }
    
    @XmlRootElement
    public static class TestBean {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }


    private TJWSEmbeddedJaxrsServer tjws;

    @Before
    public void startServer() {
        tjws = new TJWSEmbeddedJaxrsServer();
        tjws.setPort(9090);
        tjws.getRegistry().addPerRequestResource(TestResource.class);
        tjws.start();

    }

    @After
    public void stopServer() {
        tjws.stop();
    }

    @Test
    public void testHtmlError() throws Exception {
        HttpClient hc = new HttpClient();
        GetMethod gm = new GetMethod("http://localhost:9090/test");
        gm.addRequestHeader("accept", "text/html");
        int status = hc.executeMethod(gm);
        assertEquals(500, status);
        String response = gm.getResponseBodyAsString();
        assertTrue(response
                .contains("media type: text/html"));
    }

}
