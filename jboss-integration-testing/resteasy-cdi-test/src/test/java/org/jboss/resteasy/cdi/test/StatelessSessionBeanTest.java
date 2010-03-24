package org.jboss.resteasy.cdi.test;

import org.junit.Test;


public class StatelessSessionBeanTest extends AbstractResourceTest
{

   @Override
   protected String getTestPrefix()
   {
      return "statelessEjb/";
   }
   
   @Test
   public void testEjbFieldInjection()
   {
      testPlainTextReadonlyResource(BASE_URI + getTestPrefix() + "ejbFieldInjection", true);
   }

   @Override
   // WELDINT-31 This test is disabled temporarily
   public void testCdiConstructorInjection()
   {
   }
   
   
}
