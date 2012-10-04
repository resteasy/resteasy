package org.jboss.resteasy.test.providers.jackson;

import org.jboss.resteasy.annotations.providers.NoJackson;
import org.jboss.resteasy.annotations.providers.jaxb.json.BadgerFish;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import static org.jboss.resteasy.test.TestPortProvider.generateBaseUrl;
import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class JacksonTest extends BaseResourceTest
{
   public static class Product
   {
      protected String name;

      protected int id;

      public Product()
      {
      }

      public Product(int id, String name)
      {
         this.id = id;
         this.name = name;
      }

      public String getName()
      {
         return name;
      }

      public void setName(String name)
      {
         this.name = name;
      }

      public int getId()
      {
         return id;
      }

      public void setId(int id)
      {
         this.id = id;
      }
   }

   @XmlRootElement(name = "product")
   @NoJackson
   @XmlAccessorType(XmlAccessType.FIELD)
   public static class XmlProduct
   {
      @XmlAttribute
      protected String name;

      @XmlAttribute
      protected int id;

      public XmlProduct()
      {
      }

      public XmlProduct(int id, String name)
      {
         this.id = id;
         this.name = name;
      }

      public String getName()
      {
         return name;
      }

      public void setName(String name)
      {
         this.name = name;
      }

      public int getId()
      {
         return id;
      }

      public void setId(int id)
      {
         this.id = id;
      }
   }

   @Path("/products")
   public interface JacksonProxy
   {
      @GET
      @Produces("application/json")
      @Path("{id}")
      Product getProduct();

      @GET
      @Produces("application/json")
      JacksonTest.Product[] getProducts();

      @POST
      @Produces("application/foo+json")
      @Consumes("application/foo+json")
      @Path("{id}")
      Product post(@PathParam("id") int id, Product p);
   }


   @Path("/products")
   public static class JacksonService
   {

      @GET
      @Produces("application/json")
      @Path("{id}")
      public Product getProduct()
      {
         return new Product(333, "Iphone");
      }

      @GET
      @Produces("application/json")
      public Product[] getProducts()
      {

         Product[] products = {new Product(333, "Iphone"), new Product(44, "macbook")};
         return products;
      }

      @POST
      @Produces("application/foo+json")
      @Consumes("application/foo+json")
      @Path("{id}")
      public Product post(Product p)
      {
         return p;
      }

   }


   @Path("/xml/products")
   public static class XmlService
   {

      @GET
      @Produces("application/json")
      @Path("{id}")
      @BadgerFish
      public XmlProduct getProduct()
      {
         return new XmlProduct(333, "Iphone");
      }

      @GET
      @Produces("application/json")
      @NoJackson
      public XmlProduct[] getProducts()
      {

         XmlProduct[] products = {new XmlProduct(333, "Iphone"), new XmlProduct(44, "macbook")};
         return products;
      }

   }

   @Before
   public void setUp() throws Exception
   {
      dispatcher.getRegistry().addPerRequestResource(JacksonService.class);
      dispatcher.getRegistry().addPerRequestResource(XmlService.class);
   }

   @Test
   public void testJacksonString() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/products/333"));
      ClientResponse<String> response = request.get(String.class);
      System.out.println(response.getEntity());
      Assert.assertEquals(200, response.getStatus());
      Assert.assertEquals("{\"name\":\"Iphone\",\"id\":333}", response.getEntity());

      request = new ClientRequest(generateURL("/products"));
      ClientResponse<String> response2 = request.get(String.class);
      System.out.println(response2.getEntity());
      Assert.assertEquals(200, response2.getStatus());
      Assert.assertEquals("[{\"name\":\"Iphone\",\"id\":333},{\"name\":\"macbook\",\"id\":44}]", response2.getEntity());

   }

   @Test
   public void testXmlString() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/xml/products/333"));
      ClientResponse<String> response = request.get(String.class);
      System.out.println(response.getEntity());
      Assert.assertEquals(200, response.getStatus());
      Assert.assertTrue(response.getEntity().startsWith("{\"product"));


      request = new ClientRequest(generateURL("/xml/products"));
      ClientResponse<String> response2 = request.get(String.class);
      System.out.println(response2.getEntity());
      Assert.assertEquals(200, response2.getStatus());
      Assert.assertTrue(response2.getEntity().startsWith("[{\"product"));
   }

   @Test
   public void testJackson() throws Exception
   {
      ClientRequest request = new ClientRequest(generateURL("/products/333"));
      ClientResponse<Product> response = request.get(Product.class);
      Product p = response.getEntity();
      Assert.assertEquals(333, p.getId());
      Assert.assertEquals("Iphone", p.getName());
      request = new ClientRequest(generateURL("/products"));
      ClientResponse<String> response2 = request.get(String.class);
      System.out.println(response2.getEntity());
      Assert.assertEquals(200, response2.getStatus());

      request = new ClientRequest(generateURL("/products/333"));
      request.body("application/foo+json", p);
      response = request.post(Product.class);
      p = response.getEntity();
      Assert.assertEquals(333, p.getId());
      Assert.assertEquals("Iphone", p.getName());


   }

   @Test
   public void testJacksonProxy() throws Exception
   {
      JacksonProxy proxy = ProxyFactory.create(JacksonProxy.class, generateBaseUrl());
      Product p = new Product(1, "Stuff");
      p = proxy.post(1, p);
      Assert.assertEquals(1, p.getId());
      Assert.assertEquals("Stuff", p.getName());
   }
}
