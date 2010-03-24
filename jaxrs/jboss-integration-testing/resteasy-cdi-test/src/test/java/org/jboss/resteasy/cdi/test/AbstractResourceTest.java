package org.jboss.resteasy.cdi.test;

import org.junit.Test;

public abstract class AbstractResourceTest extends AbstractResteasyCdiTest
{
   abstract protected String getTestPrefix();
   
   @Test
   public void testCdiFieldInjection()
   {
      testPlainTextReadonlyResource(BASE_URI + getTestPrefix() + "fieldInjection", true);
   }
   
   @Test
   public void testCdiConstructorInjection()
   {
      testPlainTextReadonlyResource(BASE_URI + getTestPrefix() + "constructorInjection", true);
   }
   
   @Test
   public void testCdiInitializerInjection()
   {
      testPlainTextReadonlyResource(BASE_URI + getTestPrefix() + "initializerInjection", true);
   }
   
   @Test
   public void testJaxrsFieldInjection()
   {
      testPlainTextReadonlyResource(BASE_URI + getTestPrefix() + "jaxrsFieldInjection", true);
   }
   
   
   @Test
   public void testJaxrsSetterInjection()
   {
      testPlainTextReadonlyResource(BASE_URI + getTestPrefix() + "jaxrsSetterInjection", true);
   }
   
   @Test
   public void testJaxrsMethodInjection()
   {
      testPlainTextReadonlyResource(BASE_URI + getTestPrefix() + "jaxrsMethodInjection?foo=bar", "bar");
   }
   
   @Test
   public void testSubResource()
   {
      testPlainTextReadonlyResource(BASE_URI + getTestPrefix() + "subResource", "bar");
   }
}
