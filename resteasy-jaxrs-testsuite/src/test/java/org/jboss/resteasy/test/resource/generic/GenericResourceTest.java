package org.jboss.resteasy.test.resource.generic;

import javax.ws.rs.client.ClientBuilder;

import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.test.TestPortProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GenericResourceTest extends BaseResourceTest
{
   StudentInterface proxy;

   @Before
   public void setUp()
   {
      addPerRequestResource(StudentCrudResource.class);
      getProviderFactory().registerProvider(StudentReader.class);
      getProviderFactory().registerProvider(StudentWriter.class);
      ResteasyWebTarget target = (ResteasyWebTarget) ClientBuilder.newClient().target(TestPortProvider.generateBaseUrl());
      proxy = target.register(StudentReader.class).register(StudentWriter.class).proxy(StudentInterface.class);
   }

   @Test
   public void testGet()
   {
      Assert.assertTrue(proxy.get(1).getName().equals("Jozef Hartinger"));
   }

   @Test
   public void testPut()
   {
      proxy.put(2, new Student("John Doe"));
      Assert.assertTrue(proxy.get(2).getName().equals("John Doe"));
   }
}
