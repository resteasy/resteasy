package org.jboss.resteasy.test.client;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class TraceTest extends BaseResourceTest
{
   @HttpMethod("TRACE")
   @Target(value= ElementType.METHOD)
   @Retention(value= RetentionPolicy.RUNTIME)
   public @interface TRACE {

   }

   @Path("resource")
   public static class Resource {

      @Context
      UriInfo uriInfo;

      @GET
      @Path("get")
      public String get() {
         return "get";
      }

      @GET
      @Path("getnotok")
      public Response getNotOk() {
         return Response.status(Response.Status.NOT_ACCEPTABLE).build();
      }


      @HEAD
      @Path("head")
      public String head() {
         return "head";
      }

      @HEAD
      @Path("headnotok")
      public Response headNotOk() {
         return Response.status(Response.Status.NOT_ACCEPTABLE).build();
      }


      @PUT
      @Path("put")
      public String put(String value) {
         return value;
      }

      @PUT
      @Path("putnotok")
      public Response putNotOk(String value) {
         return Response.status(Response.Status.NOT_ACCEPTABLE).build();
      }


      @POST
      @Path("post")
      public String post(String value) {
         return value;
      }

      @POST
      @Path("postnotok")
      public Response postNotOk(String value) {
         return Response.status(Response.Status.NOT_ACCEPTABLE).build();
      }


      @DELETE
      @Path("delete")
      public String delete() {
         return "delete";
      }

      @DELETE
      @Path("deletenotok")
      public Response deleteNotOk() {
         return Response.status(Response.Status.NOT_ACCEPTABLE).build();
      }

      @OPTIONS
      @Path("options")
      public String options() {
         return "options";
      }

      @OPTIONS
      @Path("optionsnotok")
      public Response optionsNotOk() {
         return Response.status(Response.Status.NOT_ACCEPTABLE).build();
      }


      @TRACE
      @Path("trace")
      public String trace() {
         System.out.println("uriInfo.request: " + uriInfo.getRequestUri().toString());
         return "trace";
      }

      @TRACE
      @Path("tracenotok")
      public Response traceNotOk() {
         return Response.status(Response.Status.NOT_ACCEPTABLE).build();
      }

   }

   static Client client;

   @BeforeClass
   public static void setup() throws Exception
   {
      client = ClientBuilder.newClient();
      addPerRequestResource(Resource.class);
   }

   @Test
   public void testTrace()
   {
      Response response = client.target(generateURL("/resource/trace")).request().trace(Response.class);
      Assert.assertEquals(200, response.getStatus());
   }
   @Test
   public void testUrl() throws Exception {
      System.out.println("**** TEST URL");
      String uri = generateURL("/resource/trace");

      System.out.println(uri);
      HttpClient client = new DefaultHttpClient();
      CustomHttpTrace trace = new CustomHttpTrace(uri);
      HttpResponse response = client.execute(trace);
      Assert.assertEquals(200, response.getStatusLine().getStatusCode());
      client.getConnectionManager().shutdown();
   }

   private static class CustomHttpTrace extends HttpGet
   {
      private CustomHttpTrace(String uri)
      {
         super(uri);
      }

      @Override
      public String getMethod()
      {
         return "TrAcE";
      }
   }



}
