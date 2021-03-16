package org.jboss.resteasy.test;

import org.jboss.resteasy.plugins.server.reactor.netty.ReactorNettyContainer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:rsigal@redhat.com">Ron Sigal</a>
 * RESTEASY-1244
 *
 * @version $Revision: 1 $
 */
public class HeaderTooLongTest
{
   static String longString = "abcdefghijklmnopqrstuvwxyz";
   static
   {
      for (int i = 0; i < 10; i++)
      {
         longString += longString;
      }
   }

   @Path("/")
   public static class Resource
   {
      @GET
      @Path("/org/jboss/resteasy/test")
      public String hello(@Context HttpHeaders headers)
      {
         return "hello world";
      }
   }

   static Client client;

   @BeforeClass
   public static void setup() throws Exception
   {
      ReactorNettyContainer.start().getRegistry().addPerRequestResource(Resource.class);
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void end() throws Exception
   {
      client.close();
      ReactorNettyContainer.stop();
   }

   @Test
   public void testLongHeader() throws Exception
   {
      WebTarget target = client.target(generateURL("/org/jboss/resteasy/test"));
      Response response = target.request().header("xheader", longString).get();
      // [AG] Discuss with @crankydillo.  Yes, this is coming from Netty.  Reactor Netty
      // allows configuring the max value.  When I changed our settings as below, I get 200,
      // because the maxHeaderSize is quite large:
      //       HttpServer svrBuilder =
      //          HttpServer.create()
      //              .tcpConfiguration(this::configure)
      //              .port(configuredPort)
      //              .httpRequestDecoder(spec -> spec.maxHeaderSize(Integer.MAX_VALUE));
      //
      // The problem though..  Shouldn't it be 431 (Request Header Fields Too Large)
      // instead of 413 (Payload Too Large)?
      Assert.assertEquals(413, response.getStatus());
   }
}
