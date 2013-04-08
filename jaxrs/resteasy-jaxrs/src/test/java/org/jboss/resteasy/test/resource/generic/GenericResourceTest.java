package org.jboss.resteasy.test.resource.generic;

import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;
import static org.jboss.resteasy.test.TestPortProvider.generateBaseUrl;

public class GenericResourceTest extends BaseResourceTest
{
   StudentInterface proxy;

   @Before
   public void setUp()
   {
      addPerRequestResource(StudentCrudResource.class);
      getProviderFactory().registerProvider(StudentReader.class);
      getProviderFactory().registerProvider(StudentWriter.class);

      proxy = ProxyFactory.create(StudentInterface.class, generateBaseUrl());
   }

   @Test
   public void testGet()
   {
      assertTrue(proxy.get(1).getName().equals("Jozef Hartinger"));
   }

   @Test
   public void testPut()
   {
      proxy.put(2, new Student("John Doe"));
      assertTrue(proxy.get(2).getName().equals("John Doe"));
   }
}
