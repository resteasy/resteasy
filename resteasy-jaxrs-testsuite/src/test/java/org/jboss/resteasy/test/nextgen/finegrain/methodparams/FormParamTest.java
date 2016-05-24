package org.jboss.resteasy.test.nextgen.finegrain.methodparams;

import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Encoded;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.SortedSet;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class FormParamTest extends BaseResourceTest
{
   static Client client;

   public abstract static class ParamEntityPrototype {
      protected String value;

      public String getValue() {
         return value;
      }
   }
   public static class ParamEntityWithFromString extends ParamEntityPrototype implements
           java.lang.Comparable<ParamEntityWithFromString> {

      public static ParamEntityWithFromString fromString(String arg) {
         ParamEntityWithFromString newEntity = new ParamEntityWithFromString();
         newEntity.value = arg;
         return newEntity;
      }

      @Override
      public int compareTo(ParamEntityWithFromString o) {
         return this.value.compareTo(o.value);
      }

      @Override
      public boolean equals(Object obj) {
         return this.value.equals(obj);
      }

      @Override
      public int hashCode() {
         return this.value.hashCode();
      }
   }

   public static class ParamEntityWithConstructor extends ParamEntityPrototype {
      public ParamEntityWithConstructor(String arg){
         value = arg;
      }
   }

   public static class ParamEntityThrowsIllegaArgumentException extends ParamEntityPrototype {
      public static ParamEntityThrowsIllegaArgumentException fromString(String arg){
         throw new IllegalArgumentException("failed to parse");
      }
   }

   @Path("/")
   public static class FormResource
   {
      @POST
      @Path("form")
      @Consumes("application/x-www-form-urlencoded")
      public String post(@Encoded @FormParam("param") String param)
      {
         return param;
      }
   }

   @Path(value = "/FormParamTest/")
   public static class Resource {
      @Path(value = "/ParamEntityWithFromString")
      @POST
      @Consumes("application/x-www-form-urlencoded")
      public Response fromString(
              @Encoded @DefaultValue("FromString") @FormParam("default_argument") ParamEntityWithFromString defaultArgument) {
         return Response.ok(response(defaultArgument.getValue())).build();
      }

      public static final String response(String argument) {
         return new StringBuilder().append("CTS_FORMPARAM:").append(argument)
                 .toString();
      }

      @Path(value = "/string")
      @POST
      @Consumes("application/x-www-form-urlencoded")
      public Response string(
              @Encoded @DefaultValue("FromString") @FormParam("default_argument") String defaultArgument) {
         return Response.ok(response(defaultArgument)).build();
      }

      @Path(value = "/SortedSetFromString")
      @POST
      @Consumes("application/x-www-form-urlencoded")
      public Response sortedSetFromString(
              @Encoded @DefaultValue("SortedSetFromString") @FormParam("default_argument") SortedSet<ParamEntityWithFromString> defaultArgument) {
         return Response.ok(response(defaultArgument.first().getValue()))
                 .build();
      }

      @Path(value = "/ListConstructor")
      @POST
      @Consumes("application/x-www-form-urlencoded")
      public Response defaultListConstructor(
              @DefaultValue("ListConstructor") @FormParam("default_argument") List<ParamEntityWithConstructor> defaultArgument) {
         return Response.ok(
                 response(defaultArgument.listIterator().next().getValue()))
                 .build();
      }

      @Path(value = "/IllegalArgumentException")
      @POST
      @Consumes("application/x-www-form-urlencoded")
      public Response throwWebApplicationException(
              @DefaultValue("SortedSetFromString") @FormParam("default_argument") ParamEntityThrowsIllegaArgumentException defaultArgument) {
         return Response.ok().build();
      }




   }
      @BeforeClass
   public static void setup()
   {
      addPerRequestResource(Resource.class);
      addPerRequestResource(FormResource.class);
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void cleanup()
   {
      client.close();
   }


   private static final String DECODED = "_`'$X Y@\"a a\"";
   private static final String SENT = "_`'$X Y@\"a a\"";
   private static final String ENCODED = "_%60%27%24X+Y%40%22a+a%22";

   @Test
   public void postTest()
   {
      Entity entity = Entity.entity("param=" + ENCODED, MediaType.APPLICATION_FORM_URLENCODED_TYPE);
      Response response = client.target(generateURL("/form")).request().post(entity);
      Assert.assertEquals(response.getStatus(), 200);
      System.out.println(response.readEntity(String.class));
      response.close();
   }


   @Test
   public void nonDefaultFormParamFromStringTest()
   {
      Entity entity = Entity.entity("default_argument=" + SENT, MediaType.APPLICATION_FORM_URLENCODED_TYPE);
      Response response = client.target(generateURL("/FormParamTest/ParamEntityWithFromString")).request().post(entity);
      Assert.assertEquals(response.getStatus(), 200);
      System.out.println(response.readEntity(String.class));
      response.close();
   }

   @Test
   public void string()
   {
      Entity entity = Entity.entity("default_argument=" + SENT, MediaType.APPLICATION_FORM_URLENCODED_TYPE);
      Response response = client.target(generateURL("/FormParamTest/string")).request().post(entity);
      Assert.assertEquals(response.getStatus(), 200);
      System.out.println(response.readEntity(String.class));
      response.close();
   }

   @Test
   public void defaultFormParamFromSortedSetFromStringTest()
   {
      Entity entity = Entity.entity("default_argument=" + SENT, MediaType.APPLICATION_FORM_URLENCODED_TYPE);
      Response response = client.target(generateURL("/FormParamTest/SortedSetFromString")).request().
              header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED).post(null);
      Assert.assertEquals(response.getStatus(), 200);
      System.out.println(response.readEntity(String.class));
      response.close();
   }

   @Test
   public void defaultListConstructor()
   {
      Entity entity = Entity.entity("default_argument=" + SENT, MediaType.APPLICATION_FORM_URLENCODED_TYPE);
      Response response = client.target(generateURL("/FormParamTest/ListConstructor")).request().
              header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED).post(null);
      Assert.assertEquals(response.getStatus(), 200);
      System.out.println(response.readEntity(String.class));
      response.close();
   }

   @Test
   public void testIllegalArgumentException()
   {
      Response response = client.target(generateURL("/FormParamTest/IllegalArgumentException")).request().
              header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED).post(null);
      Assert.assertEquals(response.getStatus(), Response.Status.BAD_REQUEST.getStatusCode());
      System.out.println(response.readEntity(String.class));
      response.close();
   }



}
