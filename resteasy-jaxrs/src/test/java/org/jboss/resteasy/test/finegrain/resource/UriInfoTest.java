package org.jboss.resteasy.test.finegrain.resource;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.plugins.server.resourcefactory.SingletonResource;
import org.jboss.resteasy.test.EmbeddedContainer;
import static org.jboss.resteasy.test.TestPortProvider.*;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.net.URI;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class UriInfoTest
{
   private static Dispatcher dispatcher;

   @BeforeClass
   public static void before() throws Exception
   {
   }

   @AfterClass
   public static void after() throws Exception
   {
   }

   @Path("/")
   public static class SimpleResource
   {
      @Path("/simple")
      @GET
      public String get(@Context UriInfo info, @QueryParam("abs") String abs)
      {
         System.out.println("abs query: " + abs);
         URI base = null;
         if (abs == null)
         {
            base = createURI("/");
         }
         else
         {
            base = createURI("/" + abs + "/");
         }

         System.out.println("BASE URI: " + info.getBaseUri());
         System.out.println("Request URI: " + info.getRequestUri());
         Assert.assertEquals(base.getPath(), info.getBaseUri().getPath());
         Assert.assertEquals("/simple", info.getPath());
         return "CONTENT";
      }

      @Context
      UriInfo myInfo;

      @Path("/simple/fromField")
      @GET
      public String get(@QueryParam("abs") String abs)
      {
         System.out.println("abs query: " + abs);
         URI base = null;
         if (abs == null)
         {
            base = createURI("/");
         }
         else
         {
            base = createURI("/" + abs + "/");
         }

         System.out.println("BASE URI: " + myInfo.getBaseUri());
         System.out.println("Request URI: " + myInfo.getRequestUri());
         Assert.assertEquals(base.getPath(), myInfo.getBaseUri().getPath());
         Assert.assertEquals("/simple/fromField", myInfo.getPath());
         return "CONTENT";
      }

   }

   private void _test(HttpClient client, String path)
   {
      {
         GetMethod method = createGetMethod(path);
         try
         {
            int status = client.executeMethod(method);
            Assert.assertEquals(status, HttpResponseCodes.SC_OK);
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }
      }

   }

   @Test
   public void testUriInfoWithSingleton() throws Exception
   {
      dispatcher = EmbeddedContainer.start().getDispatcher();
      try
      {
         dispatcher.getRegistry().addResourceFactory(new SingletonResource(new SimpleResource()));
         _test(new HttpClient(), "/simple/fromField");
      }
      finally
      {
         EmbeddedContainer.stop();
      }

   }

   @Test
   public void testUriInfo() throws Exception
   {
      dispatcher = EmbeddedContainer.start().getDispatcher();
      try
      {
         dispatcher.getRegistry().addPerRequestResource(SimpleResource.class);
         _test(new HttpClient(), "/simple");
         _test(new HttpClient(), "/simple/fromField");
      }
      finally
      {
         EmbeddedContainer.stop();
      }
   }

   @Test
   public void testUriInfo2() throws Exception
   {
      dispatcher = EmbeddedContainer.start("/resteasy").getDispatcher();
      try
      {
         dispatcher.getRegistry().addPerRequestResource(SimpleResource.class);
         _test(new HttpClient(), "/resteasy/simple?abs=resteasy");
      }
      finally
      {
         EmbeddedContainer.stop();
      }
   }

   @Path("/{a}/{b}")
   public static class EncodedTemplateResource
   {
      @GET
      public String doGet(@PathParam("a") String a, @PathParam("b") String b, @Context UriInfo info)
      {
         Assert.assertEquals("a b", a);
         Assert.assertEquals("x y", b);
         Assert.assertEquals("a b", info.getPathParameters().getFirst("a"));
         Assert.assertEquals("x y", info.getPathParameters().getFirst("b"));
         Assert.assertEquals("a%20b", info.getPathParameters(false).getFirst("a"));
         Assert.assertEquals("x%20y", info.getPathParameters(false).getFirst("b"));

         List<PathSegment> decoded = info.getPathSegments(true);
         Assert.assertEquals(decoded.size(), 2);
         Assert.assertEquals("a b", decoded.get(0).getPath());
         Assert.assertEquals("x y", decoded.get(1).getPath());

         List<PathSegment> encoded = info.getPathSegments(false);
         Assert.assertEquals(encoded.size(), 2);
         Assert.assertEquals("a%20b", encoded.get(0).getPath());
         Assert.assertEquals("x%20y", encoded.get(1).getPath());
         return "content";
      }
   }

   @Test
   public void testEncodedTemplateParams() throws Exception
   {
      dispatcher = EmbeddedContainer.start().getDispatcher();
      try
      {
         dispatcher.getRegistry().addPerRequestResource(EncodedTemplateResource.class);
         _test(new HttpClient(), "/a%20b/x%20y");
      }
      finally
      {
         EmbeddedContainer.stop();
      }
   }

   @Path("/query")
   public static class EncodedQueryResource
   {
      @GET
      public String doGet(@QueryParam("a") String a, @Context UriInfo info)
      {
         Assert.assertEquals("a b", a);
         Assert.assertEquals("a b", info.getQueryParameters().getFirst("a"));
         Assert.assertEquals("a%20b", info.getQueryParameters(false).getFirst("a"));
         return "content";
      }
   }

   @Test
   public void testEncodedQueryParams() throws Exception
   {
      dispatcher = EmbeddedContainer.start().getDispatcher();
      try
      {
         dispatcher.getRegistry().addPerRequestResource(EncodedQueryResource.class);
         _test(new HttpClient(), "/query?a=a%20b");
      }
      finally
      {
         EmbeddedContainer.stop();
      }
   }
}
