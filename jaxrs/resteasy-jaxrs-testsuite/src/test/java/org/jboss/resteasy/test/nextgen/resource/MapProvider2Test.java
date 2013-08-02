package org.jboss.resteasy.test.nextgen.resource;

import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MapProvider2Test extends BaseResourceTest
{
   @Path("/")
   public static class Resource {
      @Path("map")
      @POST
      public MultivaluedMap<String, String> map(MultivaluedMap<String, String> map) {
         return map;
      }
   }




   static Client client;

   @BeforeClass
   public static void setup()
   {
      addPerRequestResource(Resource.class);
      client = ClientBuilder.newClient();
   }

   @AfterClass
   public static void cleanup()
   {
      client.close();
   }


   @Test
   public void testMapInvoke()
   {
      // writers sorted by type, mediatype, and then by app over builtin
      MultivaluedMap<String, String> map = new MultivaluedHashMap<String, String>();
      map.add("map", "map");
      Response response = client.target(generateURL("/map")).request(MediaType.APPLICATION_FORM_URLENCODED_TYPE).build("POST", Entity.entity(map, MediaType.APPLICATION_FORM_URLENCODED)).invoke();
      Assert.assertEquals(response.getStatus(), 200);
      String data = response.readEntity(String.class);
      System.out.println(data);
      Assert.assertTrue(data.contains("map"));
      response.close();
   }

   @Test
   public void testMapPost()
   {
      // writers sorted by type, mediatype, and then by app over builtin
      MultivaluedMap<String, String> map = new MultivaluedHashMap<String, String>();
      map.add("map", "map");
      Response response = client.target(generateURL("/map")).request(MediaType.APPLICATION_FORM_URLENCODED_TYPE).post(Entity.entity(map, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
      Assert.assertEquals(response.getStatus(), 200);
      String data = response.readEntity(String.class);
      System.out.println(data);
      Assert.assertTrue(data.contains("map"));
      response.close();
   }


}
