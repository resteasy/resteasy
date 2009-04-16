package org.jboss.resteasy.test.providers.jaxb.collection;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.test.BaseResourceTest;
import static org.jboss.resteasy.test.TestPortProvider.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class JsonMapTest extends BaseResourceTest
{
   @XmlRootElement
   public static class Foo
   {
      @XmlAttribute
      private String name;

      public Foo()
      {
      }

      public Foo(String name)
      {
         this.name = name;
      }

      public String getName()
      {
         return name;
      }
   }

   @Path("/map")
   public static class MyResource
   {
      @POST
      @Produces("application/json")
      @Consumes("application/json")
      public Map<String, Foo> post(Map<String, Foo> map)
      {
         Assert.assertEquals(2, map.size());
         Assert.assertNotNull(map.get("bill"));
         Assert.assertNotNull(map.get("monica"));
         Assert.assertEquals(map.get("bill").getName(), "bill");
         Assert.assertEquals(map.get("monica").getName(), "monica");
         return map;
      }

      @POST
      @Produces("application/json")
      @Consumes("application/json")
      @Path("empty")
      public Map<String, Foo> postEmpty(Map<String, Foo> map)
      {
         Assert.assertEquals(0, map.size());
         return map;
      }

      @GET
      @Produces("application/json")
      public Map<String, Foo> get()
      {
         HashMap<String, Foo> map = new HashMap<String, Foo>();
         map.put("bill", new Foo("bill"));
         map.put("monica", new Foo("monica"));
         return map;
      }
   }

   @Before
   public void setup()
   {
      addPerRequestResource(MyResource.class);

   }

   @Test
   public void testProvider() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/map"));
      ClientResponse<String> response = request.get(String.class);
      Assert.assertEquals(200, response.getStatus());
      System.out.println(response.getEntity());

      request = new ClientRequest(generateURL("/map"));
      request.body("application/json", response.getEntity());
      response = request.post();
      Assert.assertEquals(200, response.getStatus());

      request = new ClientRequest(generateURL("/map"));
      request.body("application/json", "{\"monica\":{\"foo\":{\"@name\":\"monica\"}},\"bill\":{\"foo\":{\"@name\":\"bill\"}}}");
      response = request.post();
      Assert.assertEquals(200, response.getStatus());
   }

   @Test
   public void testEmptyMap() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/map/empty"));
      request.body("application/json", "{}");
      ClientResponse<String> response = request.post(String.class);
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("{}", response.getEntity());

   }
}