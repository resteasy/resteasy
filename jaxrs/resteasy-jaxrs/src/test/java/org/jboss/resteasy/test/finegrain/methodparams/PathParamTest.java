package org.jboss.resteasy.test.finegrain.methodparams;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Set;

/**
 * Spec requires that HEAD and OPTIONS are handled in a default manner
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PathParamTest extends BaseResourceTest
{

   @Path(value = "/PathParamTest")
   public static class Resource
   {
      @GET
      @Path("/{id}")
      public Response single(@PathParam("id") String id)
      {
         return Response.ok("single=" + id).build();
      }

      @GET
      @Path("/{id}/{id1}")
      public Response two(@PathParam("id") String id,
                          @PathParam("id1") PathSegment id1)
      {
         return Response.ok("double=" + id + id1.getPath()).build();
      }

      @GET
      @Path("/{id}/{id1}/{id2}")
      public Response triple(@PathParam("id") int id,
                             @PathParam("id1") PathSegment id1,
                             @PathParam("id2") float id2)
      {
         return Response.ok("triple=" + id + id1.getPath() + id2).build();
      }

      @GET
      @Path("/{id}/{id1}/{id2}/{id3}")
      public Response quard(@PathParam("id") double id,
                            @PathParam("id1") boolean id1,
                            @PathParam("id2") byte id2,
                            @PathParam("id3") PathSegment id3)
      {
         return Response.ok("quard=" + id + id1 + id2 + id3.getPath()).build();
      }

      @GET
      @Path("/{id}/{id1}/{id2}/{id3}/{id4}")
      public Response penta(@PathParam("id") long id,
                            @PathParam("id1") String id1,
                            @PathParam("id2") short id2,
                            @PathParam("id3") boolean id3,
                            @PathParam("id4") PathSegment id4)
      {
         return Response.ok("penta=" + id + id1 + id2 + id3 + id4.getPath()).
                 build();
      }

      @Produces("text/plain")
      @GET
      @Path("/{id}/{id}/{id}/{id}/{id}/{id}")
      public Response list(@PathParam("id") List<String> id)
      {
         StringBuffer sb = new StringBuffer();
         sb.append("list=");
         for (String tmp : id)
         {
            sb.append(tmp);
         }
         return Response.ok(sb.toString()).build();
      }

      @Produces("text/plain")
      @GET
      @Path("/matrix/{id}")
      public Response matrixparamtest(@PathParam("id") PathSegment id)
      {
         StringBuffer sb = new StringBuffer();
         sb.append("matrix=");

         sb.append("/" + id.getPath());
         MultivaluedMap<String, String> matrix = id.getMatrixParameters();
         Set keys = matrix.keySet();
         for (Object key : keys)
         {
            sb.append(";" + key.toString() + "=" +
                    matrix.getFirst(key.toString()));

         }
         return Response.ok(sb.toString()).build();
      }
   }

   @Path("/digits")
   public static class Digits
   {
      @Path("{id:\\d+}")
      @GET
      public String get(@PathParam("id") int id)
      {
         Assert.assertEquals(5150, id);
         return Integer.toString(id);
      }
   }

   @Before
   public void setUp() throws Exception
   {
      dispatcher.getRegistry().addPerRequestResource(Digits.class);
      dispatcher.getRegistry().addPerRequestResource(Resource.class);
   }

   /*
    * Client invokes GET on root resource at /PathParamTest;
    *                 Verify that right Method is invoked using
    *                 PathParam primitive type List<String>.
    */
   @Test
   public void test6() throws Exception
   {

      String[] Headers = {"list=abcdef"};//, "list=fedcba"};
      HttpClient client = new HttpClient();

      for (String header : Headers)
      {
         GetMethod method = new GetMethod(TestPortProvider.generateURL("/PathParamTest/a/b/c/d/e/f"));
         method.addRequestHeader("Accept", "text/plain");
         int status = client.executeMethod(method);
         Assert.assertEquals(200, status);
         Assert.assertEquals(header, method.getResponseBodyAsString());
      }
   }

   @Test
   public void test178() throws Exception
   {
      {
         HttpClient client = new HttpClient();
         GetMethod method = new GetMethod(TestPortProvider.generateURL("/digits/5150"));
         int status = client.executeMethod(method);
         Assert.assertEquals(200, status);
      }

      {
         HttpClient client = new HttpClient();
         GetMethod method = new GetMethod(TestPortProvider.generateURL("/digits/5150A"));
         int status = client.executeMethod(method);
         Assert.assertEquals(404, status);
      }

   }


}