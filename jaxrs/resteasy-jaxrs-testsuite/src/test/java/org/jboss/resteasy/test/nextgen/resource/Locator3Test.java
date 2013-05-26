package org.jboss.resteasy.test.nextgen.resource;

import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.Encoded;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.Response;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class Locator3Test extends BaseResourceTest
{
   public static abstract class ParamEntityPrototype {
      protected String value;

      public String getValue() {
         return value;
      }
   }
   public static class ParamEntityWithConstructor extends ParamEntityPrototype {
      public ParamEntityWithConstructor(String arg){
         value = arg;
      }
   }

   public static class ParamEntityWithValueOf extends ParamEntityPrototype {

      public static ParamEntityWithValueOf valueOf(String arg) {
         ParamEntityWithValueOf newEntity = new ParamEntityWithValueOf();
         newEntity.value = arg;
         return newEntity;
      }
   }


   public static class ParamEntityWithFromString extends ParamEntityPrototype implements
           Comparable<ParamEntityWithFromString> {

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

   @Path("resource")
   public static class SubResource extends PathParamTest {

      @Path("subresource")
      public SubResource subresorce() {
         return this;
      }
   }


   public static class ParamEntityThrowingExceptionGivenByName extends
           ParamEntityThrowingWebApplicationException {

      public static final String ERROR_MSG = "ParamEntityThrowingExceptionGivenByName created unexpected error";

      public static ParamEntityThrowingExceptionGivenByName fromString(String arg) {
         Object o;
         try {
            Class<?> clazz = Class.forName(parseArg(arg));
            Constructor<?> constructor = clazz.getConstructor();
            o = constructor.newInstance();
         } catch (Exception e) {
            throw new WebApplicationException(new RuntimeException(ERROR_MSG));
         }
         throw (RuntimeException) o;
      }
   }


   public static class ParamEntityThrowingWebApplicationException extends
           ParamEntityWithFromString {

      public static ParamEntityThrowingWebApplicationException fromString(
              String arg) {
         throw new WebApplicationException(Response.Status.valueOf(parseArg(arg)));
      }

      protected static String parseArg(String arg) {
         return arg.contains("=") ? arg.replaceAll(".*=", "") : arg;
      }
   }


   @Path(value = "/PathParamTest")
   public static class PathParamTest {
      @Produces(MediaType.TEXT_HTML)
      @GET
      @Path("/{id}")
      public String single(@PathParam("id") String id) {
         return "single=" + id;
      }

      @Produces(MediaType.TEXT_HTML)
      @GET
      @Path("/{id}/{id1}")
      public String two(@PathParam("id") String id,
                        @PathParam("id1") PathSegment id1) {
         return "double=" + id + id1.getPath();
      }

      @GET
      @Path("/{id}/{id1}/{id2}")
      public String triple(@PathParam("id") int id,
                           @PathParam("id1") PathSegment id1, @PathParam("id2") float id2) {
         return "triple=" + id + id1.getPath() + id2;
      }

      @GET
      @Path("/{id}/{id1}/{id2}/{id3}")
      public String quard(@PathParam("id") double id,
                          @PathParam("id1") boolean id1, @PathParam("id2") byte id2,
                          @PathParam("id3") PathSegment id3) {
         return "quard=" + id + id1 + id2 + id3.getPath();
      }

      @GET
      @Path("/{id}/{id1}/{id2}/{id3}/{id4}")
      public String penta(@PathParam("id") long id,
                          @PathParam("id1") String id1, @PathParam("id2") short id2,
                          @PathParam("id3") boolean id3, @PathParam("id4") PathSegment id4) {
         return "penta=" + id + id1 + id2 + id3 + id4.getPath();
      }

      @Produces(MediaType.TEXT_PLAIN)
      @GET
      @Path("/{id}/{id}/{id}/{id}/{id}/{id}")
      public String list(@PathParam("id") List<String> id) {
         StringBuffer sb = new StringBuffer();
         sb.append("list=");
         for (String tmp : id) {
            sb.append(tmp);
         }
         return sb.toString();
      }

      @Produces(MediaType.TEXT_PLAIN)
      @GET
      @Path("/matrix/{id}")
      public String matrixparamtest(@PathParam("id") PathSegment id) {
         StringBuffer sb = new StringBuffer();
         sb.append("matrix=");

         sb.append("/" + id.getPath());
         MultivaluedMap<String, String> matrix = id.getMatrixParameters();
         Set<String> keys = matrix.keySet();
         for (Object key : keys) {
            sb.append(";" + key.toString() + "=" +
                    matrix.getFirst(key.toString()));

         }
         return sb.toString();
      }

      @Produces(MediaType.TEXT_PLAIN)
      @GET
      @Path("/ParamEntityWithConstructor/{id}")
      public String paramEntityWithConstructorTest(
              @DefaultValue("PathParamTest") @PathParam("id") ParamEntityWithConstructor paramEntityWithConstructor) {
         return paramEntityWithConstructor.getValue();
      }

      @Produces(MediaType.TEXT_PLAIN)
      @GET
      @Path("/ParamEntityWithFromString/{id}")
      public String paramEntityWithFromStringTest(
              @Encoded @DefaultValue("PathParamTest") @PathParam("id") ParamEntityWithFromString paramEntityWithFromString) {
         return paramEntityWithFromString.getValue();
      }

      @Produces(MediaType.TEXT_PLAIN)
      @GET
      @Path("/ParamEntityWithValueOf/{id}")
      public String paramEntityWithValueOfTest(
              @DefaultValue("PathParamTest") @PathParam("id") ParamEntityWithValueOf paramEntityWithValueOf) {
         return paramEntityWithValueOf.getValue();
      }

      @Produces(MediaType.TEXT_PLAIN)
      @GET
      @Path("/SetParamEntityWithFromString/{id}")
      public String setParamEntityWithFromStringTest(
              @DefaultValue("PathParamTest") @PathParam("id") Set<ParamEntityWithFromString> setParamEntityWithFromString) {
         return setParamEntityWithFromString.iterator().next().getValue();
      }

      @Produces(MediaType.TEXT_PLAIN)
      @GET
      @Path("/SortedSetParamEntityWithFromString/{id}")
      public String sortedSetParamEntityWithFromStringTest(
              @DefaultValue("PathParamTest") @PathParam("id") SortedSet<ParamEntityWithFromString> sortedSetParamEntityWithFromString) {
         return sortedSetParamEntityWithFromString.iterator().next().getValue();
      }

      @Produces(MediaType.TEXT_PLAIN)
      @GET
      @Path("/ListParamEntityWithFromString/{id}")
      public String listParamEntityWithFromStringTest(
              @DefaultValue("PathParamTest") @PathParam("id") List<ParamEntityWithFromString> listParamEntityWithFromString) {
         return listParamEntityWithFromString.iterator().next().getValue();
      }

      @Produces(MediaType.TEXT_PLAIN)
      @GET
      @Path("/ParamEntityThrowingWebApplicationException/{id}")
      public String paramEntityThrowingWebApplicationException(
              @PathParam("id") ParamEntityThrowingWebApplicationException paramEntityThrowingWebApplicationException) {
         return "";
      }

      @Produces(MediaType.TEXT_PLAIN)
      @GET
      @Path("/ParamEntityThrowingExceptionGivenByName/{id}")
      public String paramEntityThrowingExceptionGivenByName(
              @PathParam("id") ParamEntityThrowingExceptionGivenByName paramEntityThrowingExceptionGivenByName) {
         return "";
      }

      @DefaultValue("PathParamTest")
      @PathParam("FieldParamEntityWithConstructor")
      ParamEntityWithConstructor fieldParamEntityWithConstructor;

      @Produces(MediaType.TEXT_PLAIN)
      @GET
      @Path("/FieldParamEntityWithConstructor/{FieldParamEntityWithConstructor}")
      public String fieldEntityWithConstructorTest() {
         return fieldParamEntityWithConstructor.getValue();
      }

      @DefaultValue("PathParamTest")
      @PathParam("FieldParamEntityWithFromString")
      ParamEntityWithFromString fieldParamEntityWithFromString;

      @Produces(MediaType.TEXT_PLAIN)
      @GET
      @Path("/FieldParamEntityWithFromString/{FieldParamEntityWithFromString}")
      public String fieldEntityWithFromStringTest() {
         return fieldParamEntityWithFromString.getValue();
      }

      @DefaultValue("PathParamTest")
      @PathParam("FieldParamEntityWithValueOf")
      ParamEntityWithValueOf fieldParamEntityWithValueOf;

      @Produces(MediaType.TEXT_PLAIN)
      @GET
      @Path("/FieldParamEntityWithValueOf/{FieldParamEntityWithValueOf}")
      public String fieldEntityWithValueOfTest() {
         return fieldParamEntityWithValueOf.getValue();
      }

      @DefaultValue("PathParamTest")
      @PathParam("FieldSetParamEntityWithFromString")
      Set<ParamEntityWithFromString> fieldSetParamEntityWithFromString;

      @Produces(MediaType.TEXT_PLAIN)
      @GET
      @Path("/FieldSetParamEntityWithFromString/{FieldSetParamEntityWithFromString}")
      public String fieldSetParamEntityWithFromStringTest() {
         return fieldSetParamEntityWithFromString.iterator().next().getValue();
      }

      @DefaultValue("PathParamTest")
      @PathParam("FieldSortedSetParamEntityWithFromString")
      SortedSet<ParamEntityWithFromString> fieldSortedSetParamEntityWithFromString;

      @Produces(MediaType.TEXT_PLAIN)
      @GET
      @Path("/FieldSortedSetParamEntityWithFromString/{fieldSortedSetParamEntityWithFromString}")
      public String fieldSortedSetParamEntityWithFromStringTest() {
         return fieldSortedSetParamEntityWithFromString.iterator().next()
                 .getValue();
      }

      @DefaultValue("PathParamTest")
      @PathParam("FieldListParamEntityWithFromString")
      List<ParamEntityWithFromString> fieldListParamEntityWithFromString;

      @Produces(MediaType.TEXT_PLAIN)
      @GET
      @Path("/FieldListParamEntityWithFromString/{FieldListParamEntityWithFromString}")
      public String fieldListParamEntityWithFromStringTest() {
         return fieldListParamEntityWithFromString.iterator().next().getValue();
      }
   }



   public static class PathSegmentImpl implements PathSegment {

      public PathSegmentImpl(String id) {
         super();
         this.id = id;
      }

      private String id;

      @Override
      public MultivaluedMap<String, String> getMatrixParameters() {
         return null;
      }

      @Override
      public String getPath() {
         return id;
      }

   }



   static Client client;

   @BeforeClass
   public static void setup()
   {
      addPerRequestResource(SubResource.class);
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void cleanup()
   {
      client.close();
   }


   @Test
   public void testLocatorWithSubWithPathAnnotation()
   {
      Response response = client.target(generateURL("/resource/subresource/ParamEntityWithConstructor/ParamEntityWithConstructor=JAXRS")).request().get();
      Assert.assertEquals(response.getStatus(), 200);
      response.close();
   }

}
