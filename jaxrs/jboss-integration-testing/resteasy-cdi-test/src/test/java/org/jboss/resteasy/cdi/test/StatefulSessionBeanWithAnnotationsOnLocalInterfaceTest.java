package org.jboss.resteasy.cdi.test;

public class StatefulSessionBeanWithAnnotationsOnLocalInterfaceTest extends StatefulSessionBeanWithAnnotationsOnBeanClassTest
{

   @Override
   protected String getTestPrefix()
   {
      return "statefulEjbResourceWithAnnotationsOnLocalInterface/";
   }
   
   @Override
   // WELDINT-31 This test is disabled temporarily
   public void testCdiConstructorInjection()
   {
   }
}
