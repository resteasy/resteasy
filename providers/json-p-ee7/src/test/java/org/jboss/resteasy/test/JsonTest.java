package org.jboss.resteasy.test;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonStructure;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class JsonTest extends BaseResourceTest
{
   @Path("/test/json")
   public static class JsonTester
   {
      @Path("array")
      @POST
      @Produces("application/json")
      @Consumes("application/json")
      public JsonArray array(JsonArray array)
      {
         Assert.assertEquals(2, array.size());
         JsonObject obj = array.getJsonObject(0);
         Assert.assertTrue(obj.containsKey("name"));
         Assert.assertEquals(obj.getJsonString("name").getString(), "Bill");
         obj = array.getJsonObject(1);
         Assert.assertTrue(obj.containsKey("name"));
         Assert.assertEquals(obj.getJsonString("name").getString(), "Monica");
         return array;
      }

      @Path("object")
      @POST
      @Produces("application/json")
      @Consumes("application/json")
      public JsonObject object(JsonObject obj)
      {
         Assert.assertTrue(obj.containsKey("name"));
         Assert.assertEquals(obj.getJsonString("name").getString(), "Bill");
         return obj;
      }

      @Path("structure")
      @POST
      @Produces("application/json")
      @Consumes("application/json")
      public JsonStructure object(JsonStructure struct)
      {
         JsonObject obj = (JsonObject)struct;
         Assert.assertTrue(obj.containsKey("name"));
         Assert.assertEquals(obj.getJsonString("name").getString(), "Bill");
         return obj;
      }
   }

   static Client client;

   @BeforeClass
   public static void initClient()
   {
      client = ClientBuilder.newClient();
      addPerRequestResource(JsonTester.class);
   }


   @AfterClass
   public static void closeClient()
   {
      client.close();
   }

   @Test
   public void testObject() throws Exception
   {
      WebTarget target = client.target(generateURL("/test/json/object"));
      String json = target.request().post(Entity.json("{ \"name\" : \"Bill\" }"), String.class);
      System.out.println(json);

      JsonObject obj = Json.createObjectBuilder().add("name", "Bill").build();
      obj = target.request().post(Entity.json(obj), JsonObject.class);
      Assert.assertTrue(obj.containsKey("name"));
      Assert.assertEquals(obj.getJsonString("name").getString(), "Bill");

   }

   @Test
   public void testArray() throws Exception
   {
      WebTarget target = client.target(generateURL("/test/json/array"));
      String json = target.request().post(Entity.json("[{ \"name\" : \"Bill\" },{ \"name\" : \"Monica\" }]"), String.class);
      System.out.println(json);

      JsonArray array = Json.createArrayBuilder().add(Json.createObjectBuilder().add("name", "Bill").build())
                                                 .add(Json.createObjectBuilder().add("name", "Monica").build()).build();
      array = target.request().post(Entity.json(array), JsonArray.class);
      Assert.assertEquals(2, array.size());
      JsonObject obj = array.getJsonObject(0);
      Assert.assertTrue(obj.containsKey("name"));
      Assert.assertEquals(obj.getJsonString("name").getString(), "Bill");
      obj = array.getJsonObject(1);
      Assert.assertTrue(obj.containsKey("name"));
      Assert.assertEquals(obj.getJsonString("name").getString(), "Monica");

   }

   @Test
   public void testStructure() throws Exception
   {
      WebTarget target = client.target(generateURL("/test/json/structure"));
      String json = target.request().post(Entity.json("{ \"name\" : \"Bill\" }"), String.class);
      System.out.println(json);

      JsonObject obj = Json.createObjectBuilder().add("name", "Bill").build();
      JsonStructure structure = target.request().post(Entity.json(obj), JsonStructure.class);
      obj = (JsonObject)structure;
      Assert.assertTrue(obj.containsKey("name"));
      Assert.assertEquals(obj.getJsonString("name").getString(), "Bill");

   }


}
