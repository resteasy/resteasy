package org.jboss.resteasy.test.finegrain;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;

import javax.annotation.Resource;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * RESTEASY-1398 - false "multiple endpoints" warning
 * 
 * @author <a href="mailto:thofman@redhat.com">Tomas Hofman</a>
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MultipleEndpointsWarningTest extends BaseResourceTest
{
   @Resource
   @Path("")
   public static class Hello
   {
      @Path("unique")
      @GET
      public String unique() throws Exception {
         return "Hello";
      }

      @Path("verbs")
      @GET
      @Produces("text/plain")
      public String getVerb() throws Exception {
         return "Hello";
      }

      @Path("verbs")
      @POST
      @Produces("text/plain")
      public String postVerb() throws Exception {
         return "Hello";
      }

      @Path("verbs")
      @PUT
      @Produces("text/plain")
      public String putVerb() throws Exception {
         return "Hello";
      }

      @Path("duplicate")
      @GET
      public String duplicate1() throws Exception {
         return "Hello";
      }

      @Path("duplicate")
      @GET
      public String duplicate2() throws Exception {
         return "Hello";
      }
   }

   private class LogHandler extends Handler {

      private volatile int messagesLogged = 0;

      @Override
      public void publish(LogRecord record) {
         if (record.getMessage().contains(MESSAGE_CODE)) {
            messagesLogged++;
         }
      }

      @Override
      public void flush() {
      }

      @Override
      public void close() throws SecurityException {
      }
   }

   private static String MESSAGE_CODE = "RESTEASY002142";

   private LogHandler logHandler = new LogHandler();

   @Before
   public void setUp() throws Exception
   {
      LogManager.getLogManager().getLogger(LogMessages.LOGGER.getClass().getPackage().getName()).addHandler(logHandler);

      addPerRequestResource(Hello.class);
   }

   @After
   public void tearDown() {
      logHandler.messagesLogged = 0;
      LogManager.getLogManager().getLogger(LogMessages.LOGGER.getClass().getPackage().getName()).removeHandler(logHandler);
   }

   @Test
   public void test1_Unique() throws Exception {
      Client client = ClientBuilder.newClient();
      Response response = client.target(generateURL("/unique")).request().accept(MediaType.TEXT_PLAIN, MediaType.WILDCARD).get();
      response.close();
      Assert.assertEquals("Incorrectly logged " + MESSAGE_CODE, 0, logHandler.messagesLogged);

      response = client.target(generateURL("/unique")).request().get();
      response.close();
      Assert.assertEquals("Incorrectly logged " + MESSAGE_CODE, 0, logHandler.messagesLogged);

      response = client.target(generateURL("/unique")).request().accept(MediaType.TEXT_PLAIN).get();
      response.close();
      Assert.assertEquals("Incorrectly logged " + MESSAGE_CODE, 0, logHandler.messagesLogged);
   }

   @Test
   public void test2_DifferentVerbs() throws Exception {
      Client client = ClientBuilder.newClient();
      Response response = client.target(generateURL("/verbs")).request().accept(MediaType.TEXT_PLAIN).get();
      response.close();
      Assert.assertEquals("Incorrectly logged " + MESSAGE_CODE, 0, logHandler.messagesLogged);

      response = client.target(generateURL("/verbs")).request().accept(MediaType.TEXT_PLAIN, MediaType.WILDCARD).get();
      response.close();
      Assert.assertEquals("Incorrectly logged " + MESSAGE_CODE, 0, logHandler.messagesLogged);

      response = client.target(generateURL("/verbs")).request().get();
      response.close();
      Assert.assertEquals("Incorrectly logged " + MESSAGE_CODE, 0, logHandler.messagesLogged);
   }

   @Test
   public void test3_Duplicate() throws Exception {
      Client client = ClientBuilder.newClient();
      Response response = client.target(generateURL("/duplicate")).request().get();
      response.close();
      Assert.assertEquals(MESSAGE_CODE + " should've been logged once", 1, logHandler.messagesLogged);
   }
}
