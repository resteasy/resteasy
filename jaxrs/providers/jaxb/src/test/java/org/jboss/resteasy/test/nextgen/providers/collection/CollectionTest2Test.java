package org.jboss.resteasy.test.nextgen.providers.collection;

import org.jboss.resteasy.annotations.providers.jaxb.Wrapped;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class CollectionTest2Test extends BaseResourceTest
{
   @XmlRootElement
   @XmlAccessorType(XmlAccessType.FIELD)
   public static class Foo
   {
      @XmlAttribute
      private String test;

      public Foo()
      {
      }

      public Foo(String test)
      {
         this.test = test;
      }

      public String getTest()
      {
         return test;
      }

      public void setTest(String test)
      {
         this.test = test;
      }
   }

   @XmlRootElement(name = "foo", namespace = "http://foo.com")
   @XmlAccessorType(XmlAccessType.FIELD)
   public static class NamespacedFoo
   {
      @XmlAttribute
      private String test;

      public NamespacedFoo()
      {
      }

      public NamespacedFoo(String test)
      {
         this.test = test;
      }

      public String getTest()
      {
         return test;
      }

      public void setTest(String test)
      {
         this.test = test;
      }
   }

   @Path("/")
   public static class MyResource
   {
      @Path("/array")
      @Produces("application/xml")
      @Consumes("application/xml")
      @POST
      public Foo[] naked(Foo[] foo)
      {
         Assert.assertEquals(1, foo.length);
         Assert.assertEquals(foo[0].getTest(), "hello");
         return foo;
      }

      @Path("/list")
      @POST
      @Produces("application/xml")
      @Consumes("application/xml")
      @Wrapped(element = "list", namespace = "", prefix = "")
      public List<Foo> wrapped(@Wrapped(element = "list", namespace = "", prefix = "") List<Foo> list)
      {
         Assert.assertEquals(1, list.size());
         Assert.assertEquals(list.get(0).getTest(), "hello");
         return list;
      }


   }

   @Path("/namespaced")
   public static class MyNamespacedResource
   {
      @Path("/array")
      @Produces("application/xml")
      @Consumes("application/xml")
      @POST
      public NamespacedFoo[] naked(NamespacedFoo[] foo)
      {
         Assert.assertEquals(1, foo.length);
         Assert.assertEquals(foo[0].getTest(), "hello");
         return foo;
      }

      @Path("/list")
      @POST
      @Produces("application/xml")
      @Consumes("application/xml")
      @Wrapped(element = "list", namespace = "", prefix = "")
      public List<NamespacedFoo> wrapped(@Wrapped(element = "list", namespace = "", prefix = "") List<NamespacedFoo> list)
      {
         Assert.assertEquals(1, list.size());
         Assert.assertEquals(list.get(0).getTest(), "hello");
         return list;
      }


   }

   private static ResteasyClient client;

   @BeforeClass
   public static void setup()
   {
      addPerRequestResource(MyResource.class);
      addPerRequestResource(MyNamespacedResource.class);
      client = new ResteasyClientBuilder().build();
   }

   @AfterClass
   public static void shutdown()
   {
      client.close();
   }

   @Test
   public void testNakedArray() throws Exception
   {
      String xml = "<resteasy:collection xmlns:resteasy=\"http://jboss.org/resteasy\">"
              + "<foo test=\"hello\"/></resteasy:collection>";

      ResteasyWebTarget target = client.target(generateURL("/array"));
      Response response = target.request().post(Entity.xml(xml));
      List<Foo> list = response.readEntity(new GenericType<List<Foo>>()
      {
      });
      Assert.assertEquals(1, list.size());
      Assert.assertEquals(list.get(0).getTest(), "hello");
      response.close();

   }

   @Test
   public void testList() throws Exception
   {
      String xml = "<list>"
              + "<foo test=\"hello\"/></list>";

      ResteasyWebTarget target = client.target(generateURL("/list"));
      Response response = target.request().post(Entity.xml(xml));
      Foo[] list = response.readEntity(new GenericType<Foo[]>()
      {
      });
      Assert.assertEquals(1, list.length);
      Assert.assertEquals(list[0].getTest(), "hello");
      response.close();

   }

   @Test
   public void testNamespacedNakedArray() throws Exception
   {
      String xml = "<collection xmlns:foo=\"http://foo.com\">"
              + "<foo:foo test=\"hello\"/></collection>";

      ResteasyWebTarget target = client.target(generateURL("/namespaced/array"));
      Response response = target.request().post(Entity.xml(xml));
      List<NamespacedFoo> list = response.readEntity(new GenericType<List<NamespacedFoo>>()
      {
      });
      Assert.assertEquals(1, list.size());
      Assert.assertEquals(list.get(0).getTest(), "hello");
      response.close();

   }

   @Test
   public void testNamespacedList() throws Exception
   {
      String xml = "<list xmlns:foo=\"http://foo.com\">"
              + "<foo:foo test=\"hello\"/></list>";

      ResteasyWebTarget target = client.target(generateURL("/namespaced/list"));
      Response response = target.request().post(Entity.xml(xml));
      NamespacedFoo[] list = response.readEntity(new GenericType<NamespacedFoo[]>()
      {
      });
      Assert.assertEquals(1, list.length);
      Assert.assertEquals(list[0].getTest(), "hello");
      response.close();
   }

   @Test
   public void testBadList() throws Exception
   {
      String xml = "<bad-list>"
              + "<foo test=\"hello\"/></bad-list>";

      ResteasyWebTarget target = client.target(generateURL("/list"));
      Response response = target.request().post(Entity.xml(xml));
      Assert.assertEquals(400, response.getStatus());
      response.close();

   }

}
