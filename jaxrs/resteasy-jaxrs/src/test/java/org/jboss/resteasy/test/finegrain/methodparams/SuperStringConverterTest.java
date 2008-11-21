package org.jboss.resteasy.test.finegrain.methodparams;

import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.spi.StringConverter;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.ext.Provider;

public class SuperStringConverterTest extends BaseResourceTest
{
   public static class Person
   {
      private final String name;

      public Person(String name)
      {
         this.name = name;
      }

      public String getName()
      {
         return name;
      }
   }

   @Provider
   public static class PersonConverter extends SuperPersonConverter
   {
   }

   public abstract static class SuperPersonConverter implements StringConverter<Person>
   {
      public Person fromString(String value)
      {
         return new Person(value);
      }

      public String toString(Person value)
      {
         return value.getName();
      }
   }

   @Path("/")
   public static class MyResource
   {
      @Path("person/{person}")
      @PUT
      public void put(@PathParam("person") Person p)
      {
         Assert.assertEquals(p.getName(), "name");
      }
   }

   @Before
   public void setUp() throws Exception
   {
      dispatcher.getProviderFactory().addStringConverter(PersonConverter.class);
      dispatcher.getRegistry().addPerRequestResource(MyResource.class);
   }

   @Path("/")
   public static interface MyClient
   {
      @Path("person/{person}")
      @PUT
      void put(@PathParam("person") Person p);
   }

   @Test
   public void testPerson() throws Exception
   {
      MyClient client = ProxyFactory.create(MyClient.class, "http://localhost:8081");
      Person person = new Person("name");
      client.put(person);
   }
}
