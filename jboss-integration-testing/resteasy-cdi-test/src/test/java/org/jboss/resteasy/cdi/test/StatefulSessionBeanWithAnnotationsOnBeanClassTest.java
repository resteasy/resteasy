package org.jboss.resteasy.cdi.test;

import org.junit.Test;


public class StatefulSessionBeanWithAnnotationsOnBeanClassTest extends ResourceTest
{

   @Override
   protected String getTestPrefix()
   {
      return "statefulEjbResourceWithAnnotationsOnBeanClass/";
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

   @Override
   // This test is disabled temporarily
   public void testSubResource()
   {
   }
   
   
}
