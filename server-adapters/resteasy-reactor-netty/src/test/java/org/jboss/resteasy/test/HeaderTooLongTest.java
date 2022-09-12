package org.jboss.resteasy.test;

import org.jboss.resteasy.plugins.server.reactor.netty.ReactorNettyContainer;
import org.jboss.resteasy.plugins.server.reactor.netty.ReactorNettyJaxrsServer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:rsigal@redhat.com">Ron Sigal</a>
 * RESTEASY-1244
 *
 * @version $Revision: 1 $
 */
public class HeaderTooLongTest
{
   private static final int MAX_HEADER_SIZE = 10;

   final String longString =
       IntStream.range(0, MAX_HEADER_SIZE + 1)
           .mapToObj(i -> "a")
           .collect(Collectors.joining());

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
      final ReactorNettyJaxrsServer reactorNettyJaxrsServer = new ReactorNettyJaxrsServer();
      reactorNettyJaxrsServer.setDecoderSpecFn(spec -> spec.maxHeaderSize(MAX_HEADER_SIZE));
      ReactorNettyContainer.start(reactorNettyJaxrsServer).getRegistry().addPerRequestResource(Resource.class);
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
