package org.jboss.resteasy.test.experiment;

import org.jboss.resteasy.util.Types;
import org.junit.Test;

public class InterfaceTypeExperiment
{
   class C {}
   interface I<T> {}
   interface J extends I<C> {}
   class D implements J {}
   class E implements I<C> {}
   
   @Test
   public void testType() throws Exception
   {
      System.out.println(Types.getTemplateParameterOfInterface(D.class, J.class));
      System.out.println(Types.getTemplateParameterOfInterface(E.class, I.class));
   }
}

