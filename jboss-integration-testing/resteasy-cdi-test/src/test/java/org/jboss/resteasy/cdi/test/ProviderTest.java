package org.jboss.resteasy.cdi.test;

import org.junit.Test;


public class ProviderTest extends AbstractResteasyCdiTest
{
   @Test
   public void testCdiFieldInjection()
   {
      testPlainTextReadonlyResource(BASE_URI + "resource/providers", "CDI field injection: true");
   }
   
   @Test
   public void testCdiConstructorInjection()
   {
      testPlainTextReadonlyResource(BASE_URI + "resource/providers", "CDI constructor injection: true");
   }
   
   @Test
   public void testCdiInitializerInjection()
   {
      testPlainTextReadonlyResource(BASE_URI + "resource/providers", "CDI initializer injection: true");
   }
   
   @Test
   public void testJaxrsFieldInjection()
   {
      testPlainTextReadonlyResource(BASE_URI + "resource/providers", "JAX-RS field injection: true");
   }
}
