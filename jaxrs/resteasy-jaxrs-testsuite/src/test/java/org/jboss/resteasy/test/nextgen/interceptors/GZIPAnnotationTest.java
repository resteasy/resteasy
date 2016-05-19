package org.jboss.resteasy.test.nextgen.interceptors;

import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;

import org.jboss.resteasy.annotations.GZIP;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;

/**
 * RESTEASY-1265
 * 
 * @author Shun Tanaka
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @date
 */
public class GZIPAnnotationTest
{
   protected static ResteasyDeployment deployment;
   protected static Dispatcher dispatcher;
   protected static LogMessages log = LogMessages.LOGGER;

   @Path("")
   public interface TestInterface
   {
      @Path("/foo")
      @Consumes("text/plain")
      @Produces("text/plain")
      @GZIP
      @POST
      public String getFoo(@GZIP String request);
   }

   @Path("")
   public static class TestResource implements TestInterface
   {
      @Context
      HttpHeaders headers;

      @Path("/foo")
      @Consumes("text/plain")
      @Produces("text/plain")
      @GZIP
      @POST
      @Override
      public String getFoo(String request)
      {
         if ("test".equals(request))
         {
            String contentEncoding = headers.getRequestHeader(HttpHeaders.CONTENT_ENCODING).get(0);
            log.info("server Content-Encoding: " + contentEncoding);
            String acceptEncoding = headers.getRequestHeader(HttpHeaders.ACCEPT_ENCODING).get(0);
            log.info("server Accept-Encoding: " + acceptEncoding);
            return contentEncoding + "|" + acceptEncoding;
         }
         else
         {
            throw new RuntimeException("request != \"test\"");
         }
      }
   }

   @Provider
   public static class TestInterceptor implements ReaderInterceptor
   {
      @Override
      public Object aroundReadFrom(ReaderInterceptorContext ctx) throws IOException
      {
         log.info("client Content-Encoding: " + ctx.getHeaders().get(HttpHeaders.CONTENT_ENCODING));
         if (ctx.getHeaders().get(HttpHeaders.CONTENT_ENCODING).get(0).contains("gzip"))
         {
            return ctx.proceed();
         }
         else
         {
            throw new RuntimeException("no gzip in Content-Encoding");
         }
      }
   }

   @Before
   public void before() throws Exception
   {
      deployment = EmbeddedContainer.start();
      dispatcher = deployment.getDispatcher();
      deployment.getRegistry().addPerRequestResource(TestResource.class);
   }

   @After
   public void after() throws Exception
   {
      EmbeddedContainer.stop();
      dispatcher = null;
      deployment = null;
   }

   @Test
   public void testGZIP()
   {
      Client client = ClientBuilder.newClient();
      ResteasyWebTarget target = (ResteasyWebTarget) client.target("http://localhost:8081/");
      target.register(TestInterceptor.class);
      TestInterface resource = target.proxy(TestInterface.class);
      String s = resource.getFoo("test");
      log.info("response: " + s);
      Assert.assertTrue(s.contains("gzip"));
      Assert.assertTrue(s.substring(s.indexOf("gzip") + 4).contains("gzip"));
   }
}
