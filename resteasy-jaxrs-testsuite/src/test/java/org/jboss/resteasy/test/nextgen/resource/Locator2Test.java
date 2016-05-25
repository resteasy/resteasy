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
public class Locator2Test extends BaseResourceTest
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

   @Path("resource")
   public static class LocatorResource extends MiddleResource {

      @Path("locator/{id1}")
      public MiddleResource locatorHasArguments(@PathParam("id1") String id1) {
         return new MiddleResource(id1);
      }

      @Path("locator/{id1}/{id2}")
      public MiddleResource locatorHasArguments(@PathParam("id1") String id1,
                                                @PathParam("id2") String id2) {
         return new MiddleResource(id1, id2);
      }

      @Path("locator/{id1}/{id2}/{id3}")
      public MiddleResource locatorHasArguments(@PathParam("id1") String id1,
                                                @PathParam("id2") String id2, @PathParam("id3") String id3) {
         return new MiddleResource(id1, id2, id3);
      }

      @Path("locator/{id1}/{id2}/{id3}/{id4}")
      public MiddleResource locatorHasArguments(@PathParam("id1") String id1,
                                                @PathParam("id2") String id2, @PathParam("id3") String id3,
                                                @PathParam("id4") String id4) {
         return new MiddleResource(id1, id2, id3, id4);
      }

      @Path("locator/{id1}/{id2}/{id3}/{id4}/{id5}")
      public MiddleResource locatorHasArguments(@PathParam("id1") String id1,
                                                @PathParam("id2") String id2, @PathParam("id3") String id3,
                                                @PathParam("id4") String id4, @PathParam("id5") String id5) {
         return new MiddleResource(id1, id2, id3, id4, id5);
      }

      @Path("locator/{id1}/{id2}/{id3}/{id4}/{id5}/{id6}")
      public MiddleResource locatorHasArguments(@PathParam("id1") String id1,
                                                @PathParam("id2") String id2, @PathParam("id3") String id3,
                                                @PathParam("id4") String id4, @PathParam("id5") String id5,
                                                @PathParam("id6") String id6) {
         return new MiddleResource(id1, id2, id3, id4, id5, id6);
      }

      @Path("/locator/ParamEntityThrowingWebApplicationException/{id}")
      public MiddleResource locatorHasArguments(
              @PathParam("id") ParamEntityThrowingWebApplicationException paramEntityThrowingWebApplicationException) {
         return null;
      }

      @Path("/locator/ParamEntityThrowingExceptionGivenByName/{id}")
      public MiddleResource locatorHasArguments(
              @PathParam("id") ParamEntityThrowingExceptionGivenByName paramEntityThrowingExceptionGivenByName) {
         return null;
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


   public static class MiddleResource extends PathParamTest {

      private final String returnValue;

      public MiddleResource() {
         returnValue = null;
      }

      protected MiddleResource(String id) {
         returnValue = single(id);
      }

      protected MiddleResource(String id1, String id2) {
         if ("ParamEntityWithConstructor".equals(id1))
            returnValue = paramEntityWithConstructorTest(new ParamEntityWithConstructor(
                    id2));
         else if ("ParamEntityWithFromString".equals(id1))
            returnValue = paramEntityWithFromStringTest(ParamEntityWithFromString
                    .fromString(id2));
         else if ("ParamEntityWithValueOf".equals(id1))
            returnValue = paramEntityWithValueOfTest(ParamEntityWithValueOf
                    .valueOf(id2));
         else if ("SetParamEntityWithFromString".equals(id1)) {
            returnValue = setParamEntityWithFromStringTest(Collections
                    .singleton(ParamEntityWithFromString.fromString(id2)));
         } else if ("ListParamEntityWithFromString".equals(id1)) {
            returnValue = listParamEntityWithFromStringTest(Collections
                    .singletonList(ParamEntityWithFromString.fromString(id2)));
         } else
            returnValue = two(id1, new PathSegmentImpl(id2));
      }

      protected MiddleResource(String id1, String id2, String id3) {
         returnValue = triple(Integer.parseInt(id1), new PathSegmentImpl(id2),
                 Float.parseFloat(id3));
      }

      protected MiddleResource(String id1, String id2, String id3, String id4) {
         returnValue = quard(Double.parseDouble(id1), Boolean.parseBoolean(id2),
                 Byte.parseByte(id3), new PathSegmentImpl(id4));
      }

      protected MiddleResource(String id1, String id2, String id3, String id4,
                               String id5) {
         returnValue = penta(Long.parseLong(id1), id2, Short.parseShort(id3),
                 Boolean.parseBoolean(id4), new PathSegmentImpl(id5));
      }

      protected MiddleResource(String id1, String id2, String id3, String id4,
                               String id5, String id6) {
         List<String> list = Arrays.asList(id1, id2, id3, id4, id5, id6);
         returnValue = list(list);
      }

      @POST
      public String returnValue() {
         return returnValue;
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
      addPerRequestResource(LocatorResource.class);
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
      Response response = client.target(generateURL("/resource/locator/ParamEntityWithConstructor/ParamEntityWithConstructor=JAXRS")).request().post(null);
      Assert.assertEquals(response.getStatus(), 200);
      response.close();
   }

   //@Test
   public void testIllegalArgument()
   {
      Response response = client.target(generateURL("/resource/locator/ParamEntityThrowingExceptionGivenByName/java.lang.IllegalArgumentException")).request().post(null);
      Assert.assertEquals(response.getStatus(), 200);
      response.close();
   }




}
