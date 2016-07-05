package org.jboss.resteasy.test.nextgen.XmlJavaTypeAdapter;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * RESTEASY-1088
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Mar 28, 2015
 */
public class XmlJavaTypeAdapterTest
{
   protected static ResteasyDeployment deployment;
   protected static Dispatcher dispatcher;
   
   @XmlRootElement(name="human")
   @XmlAccessorType(XmlAccessType.FIELD)
   public static class Human
   {
      @XmlElement
      private String name;

      public String getName()
      {
         return name;
      }

      public void setName(String name)
      {
         this.name = name;
      }
   }
   
   @XmlJavaTypeAdapter(AlienAdapter.class)
   public static class Alien
   {
      private String name;

      public String getName()
      {
         return name;
      }

      public void setName(String name)
      {
         this.name = name;
      }
      
      public boolean equals(Object o)
      {
         if (!(o instanceof Alien))
         {
            return false;
         }
         return name.equals(Alien.class.cast(o).name);
      }
   }

   public static class Tralfamadorean extends Alien
   {
   }
   
   public static class AlienAdapter extends XmlAdapter<Human, Alien>
   {
      public static int marshalCounter;
      public static int unmarshalCounter;
      
      public static void reset()
      {
         marshalCounter = 0;
         unmarshalCounter = 0;
         System.out.println("reset()");
      }
      
      @Override
      public Human marshal(Alien alien) throws Exception
      {
         System.out.println("Entering AlienAdapter.marshal()");
         marshalCounter++;
         Human human = new Human();
         human.setName(reverse(alien.getName()));
         return human;
      }

      @Override
      public Alien unmarshal(Human human) throws Exception
      {
         System.out.println("Entering AlienAdapter.unmarshal()");
         unmarshalCounter++;
         Alien alien = new Alien();
         alien.setName(reverse(human.getName()));
         return alien;
      }  
   }
   
   @XmlRootElement
   @XmlAccessorType(XmlAccessType.FIELD)
   public static class Foo
   {
      @XmlJavaTypeAdapter(AlienAdapter.class)
      @XmlElement
      Alien alien;
      
      public void setName(String name)
      {
         alien = new Alien();
         alien.setName(name);
      }
      
      public String toString()
      {
         return "Foo[Alien[" + alien.getName() + "]]: " + super.toString();
      }
      
      public boolean equals(Object o)
      {
         if (! (o instanceof Foo))
         {
            return false;
         }
         Foo foo = Foo.class.cast(o);
         return alien.getName().equals(foo.alien.getName());
      }
   }
   
   @Resource                                                                                             
   @Path("")
   public static class TestResource
   {
      @POST
      @Path("foo/foo")
      @Consumes("application/xml")
      @Produces("application/xml")
      public Foo foo(Foo foo)
      {
         System.out.println("foo: \"" + foo + "\"");
         return foo;
      }
      
      @POST
      @Path("string")
      @Produces("text/plain")
      public String string(String foo)
      {
         System.out.println("string: \"" + foo + "\"");
         return foo.toString();
      }
      
      @POST
      @Path("human")
      @Produces("text/plain")
      public String human(Human human)
      {
         System.out.println("human: \"" + human.getName() + "\"");
         return human.getName();
      }
      
      @POST
      @Path("alien")
      @Produces("text/plain")
      public String alien(Alien alien)
      {
         System.out.println("human: \"" + alien.getName() + "\"");
         return alien.getName();
      }
      
      @POST
      @Path("list/alien")
      @Consumes("application/xml")
      @Produces("application/xml")
      public List<Alien> listAlien(List<Alien> list)
      {
         System.out.println("entering listAlien()");
         return list;
      }
      
      @POST
      @Path("array/alien")
      @Consumes("application/xml")
      @Produces("application/xml")
      public Alien[] arrayAlien(Alien[] array)
      {
         System.out.println("entering arrayAlien()");
         return array;
      }
      
      @POST
      @Path("map/alien")
      @Consumes("application/xml")
      @Produces("application/xml")
      public Map<String, Alien> mapAlien(Map<String, Alien> map)
      {
         System.out.println("entering mapAlien()");
         return map;
      }
      
      @POST
      @Path("list/human")
      @Consumes("application/xml")
      @Produces("text/plain")
      public String listHuman(List<Human> list)
      {
         String result = "";
         for (Iterator<Human> it = list.iterator(); it.hasNext(); )
         {
            String name = it.next().getName();
            result += "|" + name;
         }
         return result;
      }
   }
   
   @BeforeClass
   public static void beforeClass() throws Exception
   {
      deployment = EmbeddedContainer.start();
      dispatcher = deployment.getDispatcher();
      deployment.getRegistry().addPerRequestResource(TestResource.class);
   }
   
   @AfterClass
   public static void afterClass() throws Exception
   {
      EmbeddedContainer.stop();
      dispatcher = null;
      deployment = null;
   }
   
   @Before
   public void before() throws Exception
   {
      AlienAdapter.reset();
   }
   
   //@Test
   public void tmp() throws Exception
   {
//      {
//         JAXBContext context = JAXBContext.newInstance(Alien.class, AlienAdapter.class);
//         Marshaller marshaller = context.createMarshaller();
//         ByteArrayOutputStream baos = new ByteArrayOutputStream();
//         Alien alien = new Alien();
//         alien.setName("bill");
//         marshaller.marshal(alien, baos);
//         String s = new String(baos.toByteArray());
//         System.out.println("s: " + s);
//      }
      {
//         JAXBContext context = JAXBContext.newInstance(Alien.class, AlienAdapter.class, Foo.class);
         JAXBContext context = JAXBContext.newInstance(Foo.class);
         Marshaller marshaller = context.createMarshaller();
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         Foo foo = new Foo();
         foo.setName("bill");
         marshaller.marshal(foo, baos);
         String s = new String(baos.toByteArray());
         System.out.println("s: " + s);
         Unmarshaller unmarshaller = context.createUnmarshaller();
         Object o = unmarshaller.unmarshal(new ByteArrayInputStream(baos.toByteArray()));
         System.out.println("o: " + o);
      }
   }
   
   //@Test
   public void testPostHuman()
   {
      ResteasyClient client = new ResteasyClientBuilder().build();
      ResteasyWebTarget target = client.target(generateURL("/human"));
      Human human = new Human();
      human.setName("bill");
      String response = target.request().post(Entity.entity(human, MediaType.APPLICATION_XML_TYPE), String.class);
      System.out.println("response: \"" + response + "\"");
      Assert.assertEquals("bill", response);
      client.close();
   }
   
   //@Test
   public void testPostAlien()
   {
      ResteasyClient client = new ResteasyClientBuilder().build();
      ResteasyWebTarget target = client.target(generateURL("/alien"));
      Alien alien = new Alien();
      alien.setName("bill");
      String response = target.request().post(Entity.entity(alien, MediaType.APPLICATION_XML_TYPE), String.class);
      System.out.println("response: \"" + response + "\"");
      Assert.assertEquals("Alien[bill]", response);
      client.close();
   }
   
   //@Test
   public void testPostFooToFoo()
   {
      ResteasyClient client = new ResteasyClientBuilder().build();
      ResteasyWebTarget target = client.target(generateURL("/foo/foo"));
      Foo foo = new Foo();
      foo.setName("bill");
      Foo response = target.request().post(Entity.entity(foo, MediaType.APPLICATION_XML_TYPE), Foo.class);
      System.out.println("response: \"" + response + "\"");
      Assert.assertEquals(foo, response);
      client.close();
   }
   
   //@Test
   public void testPostFooToString()
   {
      ResteasyClient client = new ResteasyClientBuilder().build();
      ResteasyWebTarget target = client.target(generateURL("/foo/foo"));
      Foo foo = new Foo();
      foo.setName("bill");
      String response = target.request().post(Entity.entity(foo, MediaType.APPLICATION_XML_TYPE), String.class);
      System.out.println("response: \"" + response + "\"");
      Assert.assertTrue(response.contains("<foo><alien><name>llib</name></alien></foo>"));
      client.close();
   }
   
   //@Test
   public void testPostString()
   {
      ResteasyClient client = new ResteasyClientBuilder().build();
      ResteasyWebTarget target = client.target(generateURL("/string"));
      Foo foo = new Foo();
      foo.setName("bill");
      String response = target.request().post(Entity.entity(foo, MediaType.APPLICATION_XML_TYPE), String.class);
      System.out.println("response: \"" + response + "\"");
      Assert.assertEquals("Alien[bill]", response);
      client.close();
   }

   //@Test
   public void testPostHumanList()
   {
      ResteasyClient client = new ResteasyClientBuilder().build();
      ResteasyWebTarget target = client.target(generateURL("/list/human"));
      List<Human> list = new ArrayList<Human>();
      Human human = new Human();
      human.setName("bill");
      list.add(human);
      human = new Human();
      human.setName("bob");
      list.add(human);
      GenericEntity<List<Human>> entity = new GenericEntity<List<Human>>(list) {};
      String response = target.request().post(Entity.entity(entity, MediaType.APPLICATION_XML_TYPE), String.class);
      System.out.println("response: \"" + response + "\"");
      Assert.assertEquals("|bill|bob", response);
      client.close();
   }
   
   @Test
   public void testPostAlienList()
   {
      ResteasyClient client = new ResteasyClientBuilder().build();
      ResteasyWebTarget target = client.target(generateURL("/list/alien"));
      List<Alien> list = new ArrayList<Alien>();
      Alien alien1 = new Alien();
      alien1.setName("bill");
      list.add(alien1);
      Alien alien2 = new Alien();
      alien2.setName("bob");
      list.add(alien2);
      GenericEntity<List<Alien>> entity = new GenericEntity<List<Alien>>(list) {};
      GenericType<List<Alien>> alienListType = new GenericType<List<Alien>>() {};
      List<Alien> response = target.request().post(Entity.entity(entity, MediaType.APPLICATION_XML_TYPE), alienListType);
      System.out.println("response: \"" + response + "\"");
      Assert.assertEquals(2, response.size());
      Assert.assertTrue(response.contains(alien1));
      Assert.assertTrue(response.contains(alien2));
      Assert.assertEquals(4, AlienAdapter.marshalCounter);
      Assert.assertEquals(4, AlienAdapter.unmarshalCounter);
      client.close();
   }
   
   @Test
   public void testPostAlienArray()
   {
      ResteasyClient client = new ResteasyClientBuilder().build();
      ResteasyWebTarget target = client.target(generateURL("/array/alien"));
      Alien[] array = new Alien[2];
      Alien alien1 = new Alien();
      alien1.setName("bill");
      array[0] = alien1;
      Alien alien2 = new Alien();
      alien2.setName("bob");
      array[1] = alien2;
      GenericEntity<Alien[]> entity = new GenericEntity<Alien[]>(array) {};
      GenericType<Alien[]> alienArrayType = new GenericType<Alien[]>() {};
      Alien[] response = target.request().post(Entity.entity(entity, MediaType.APPLICATION_XML_TYPE), alienArrayType);
      System.out.println("response: \"" + response + "\"");
      Assert.assertEquals(2, response.length);
      Assert.assertTrue((alien1.equals(response[0]) && alien2.equals(response[1])) || (alien1.equals(response[1]) && alien2.equals(response[0])));
      Assert.assertEquals(4, AlienAdapter.marshalCounter);
      Assert.assertEquals(4, AlienAdapter.unmarshalCounter);
      client.close();
   }
   
   @Test
   public void testPostAlienMap()
   {
      ResteasyClient client = new ResteasyClientBuilder().build();
      ResteasyWebTarget target = client.target(generateURL("/map/alien"));
      Map<String, Alien> map = new HashMap<String, Alien>();
      Alien alien1 = new Alien();
      alien1.setName("bill");
      map.put("abc", alien1);
      Alien alien2 = new Alien();
      alien2.setName("bob");
      map.put("xyz", alien2);
      GenericEntity<Map<String, Alien>> entity = new GenericEntity<Map<String, Alien>>(map) {};
      GenericType<Map<String, Alien>> alienMapType = new GenericType<Map<String, Alien>>() {};
      Map<String, Alien> response = target.request().post(Entity.entity(entity, MediaType.APPLICATION_XML_TYPE), alienMapType);
      System.out.println("response: \"" + response + "\"");
      Assert.assertEquals(2, response.size());
      Assert.assertTrue(alien1.equals(response.get("abc")));
      Assert.assertTrue(alien2.equals(response.get("xyz")));
      Assert.assertEquals(4, AlienAdapter.marshalCounter);
      Assert.assertEquals(4, AlienAdapter.unmarshalCounter);
      client.close();
   }
   
   @Test
   public void testPostTralfamadoreanList()
   {
      ResteasyClient client = new ResteasyClientBuilder().build();
      ResteasyWebTarget target = client.target(generateURL("/list/alien"));
      List<Alien> list = new ArrayList<Alien>();
      Tralfamadorean tralfamadorean1 = new Tralfamadorean();
      tralfamadorean1.setName("bill");
      list.add(tralfamadorean1);
      Tralfamadorean tralfamadorean2 = new Tralfamadorean();
      tralfamadorean2.setName("bob");
      list.add(tralfamadorean2);
      GenericEntity<List<Alien>> entity = new GenericEntity<List<Alien>>(list) {};
      GenericType<List<Alien>> alienListType = new GenericType<List<Alien>>() {};
      List<Alien> response = target.request().post(Entity.entity(entity, MediaType.APPLICATION_XML_TYPE), alienListType);
      System.out.println("response: \"" + response + "\"");
      Assert.assertEquals(2, response.size());
      Assert.assertTrue(response.contains(tralfamadorean1));
      Assert.assertTrue(response.contains(tralfamadorean2));
      Assert.assertEquals(4, AlienAdapter.marshalCounter);
      Assert.assertEquals(4, AlienAdapter.unmarshalCounter);
   }
   
   protected static String reverse(String s)
   {
      return new StringBuilder(s).reverse().toString();
   }
}
