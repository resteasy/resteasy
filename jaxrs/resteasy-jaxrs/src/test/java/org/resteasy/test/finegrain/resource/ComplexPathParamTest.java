package org.resteasy.test.finegrain.resource;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.resteasy.Dispatcher;
import org.resteasy.test.EmbeddedContainer;
import org.resteasy.util.HttpResponseCodes;
import org.resteasy.util.SegmentInfo;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ComplexPathParamTest
{
   private static Dispatcher dispatcher;

   @Path("/")
   public static class ExtensionResource
   {
      @GET
      @Path("/{1},{2}/{3}/blah{4}-{5}ttt")
      public String get(@PathParam("1")int one,
                        @PathParam("2")int two,
                        @PathParam("3")int three,
                        @PathParam("4")int four,
                        @PathParam("5")int five)
      {
         Assert.assertEquals(one, 1);
         Assert.assertEquals(two, 2);
         Assert.assertEquals(three, 3);
         Assert.assertEquals(four, 4);
         Assert.assertEquals(five, 5);
         return "hello";
      }

   }

   @Path("/tricky")
   public static class TrickyResource
   {
      @GET
      @Path("{hello}")
      public String getHello(@PathParam("hello")int one)
      {
         Assert.assertEquals(one, 1);
         return "hello";
      }


      @GET
      @Path("{1},{2}")
      public String get2Groups(@PathParam("1")int one,
                               @PathParam("2")int two)
      {
         Assert.assertEquals(1, one);
         Assert.assertEquals(2, two);
         return "2Groups";
      }

      @GET
      @Path("h{1}")
      public String getPrefixed(@PathParam("1")int one)
      {
         Assert.assertEquals(1, one);
         return "prefixed";
      }
   }

   @Path("/unlimited")
   public static class UnlimitedResource
   {
      @Path(value = "{1}-{rest}", limited = false)
      @GET
      public String get(@PathParam("1")int one,
                        @PathParam("rest")String rest)
      {
         Assert.assertEquals(1, one);
         Assert.assertEquals("on/and/on", rest);
         return "ok";
      }
   }

   @BeforeClass
   public static void before() throws Exception
   {
   }

   @AfterClass
   public static void after() throws Exception
   {
   }

   @Test
   public void testSegmentInfo()
   {
      List<SegmentInfo> segments = new ArrayList<SegmentInfo>();
      segments.add(new SegmentInfo("{hello}"));
      segments.add(new SegmentInfo("foo{hello}"));
      segments.add(new SegmentInfo("{goodbye}foo{hello}"));
      Collections.sort(segments);
      for (SegmentInfo segment : segments) System.out.println(segment.getExpression());
   }

   private void _test(HttpClient client, String uri, String body)
   {
      {
         GetMethod method = new GetMethod(uri);
         try
         {
            int status = client.executeMethod(method);
            Assert.assertEquals(status, HttpResponseCodes.SC_OK);
            Assert.assertEquals(body, method.getResponseBodyAsString());
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }
      }

   }


   @Test
   public void testIt() throws Exception
   {
      dispatcher = EmbeddedContainer.start();
      try
      {
         dispatcher.getRegistry().addPerRequestResource(ExtensionResource.class);
         dispatcher.getRegistry().addPerRequestResource(TrickyResource.class);
         dispatcher.getRegistry().addPerRequestResource(UnlimitedResource.class);
         HttpClient client = new HttpClient();
         _test(client, "http://localhost:8081/1,2/3/blah4-5ttt", "hello");
         _test(client, "http://localhost:8081/tricky/1,2", "2Groups");
         _test(client, "http://localhost:8081/tricky/h1", "prefixed");
         _test(client, "http://localhost:8081/tricky/1", "hello");
         _test(client, "http://localhost:8081/unlimited/1-on/and/on", "ok");
      }
      finally
      {
         EmbeddedContainer.stop();
      }

   }

}