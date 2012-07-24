package org.jboss.resteasy.spring.test.javaconfig;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;

import static org.junit.Assert.*;

/**
 * This test will verify that the resource invoked by RESTEasy has been
 * initialized by spring when defined using spring's JavaConfig.
 */
public class JavaConfigTest {
    private static final String CONTEXT_PATH = "/";
    private static final String BASE_URL = "http://localhost:9092";
    private static final String PATH = "/rest/invoke";

   @BeforeClass
   public static void before() throws Exception {
      Server server = new Server(9092);
      WebAppContext context = new WebAppContext();
      context.setResourceBase("src/test/resources/javaconfig");
      context.setContextPath(CONTEXT_PATH);
      context.setParentLoaderPriority(true);
      server.setHandler(context);
      server.start();
   }

   @Test
   public void test() throws Exception {
       ClientRequest request = new ClientRequest(BASE_URL + CONTEXT_PATH + PATH);
       @SuppressWarnings("unchecked")
    ClientResponse<String> response = request.get();
       assertEquals("unexpected response code", 200, response.getResponseStatus().getStatusCode());
       assertEquals("unexpected response msg", "hello", response.getEntity(String.class));
   }
}
