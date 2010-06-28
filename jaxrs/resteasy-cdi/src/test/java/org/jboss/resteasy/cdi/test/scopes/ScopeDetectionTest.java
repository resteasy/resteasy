package org.jboss.resteasy.cdi.test.scopes;

import org.jboss.resteasy.cdi.Utils;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;


public class ScopeDetectionTest
{
   @Test
   public void testImplicitlyDeclaredScope()
   {
      assertTrue(Utils.isScopeDefined(Resource1.class));
   }

   @Test
   public void testScopeDeclaredInStereotype()
   {
      assertTrue(Utils.isScopeDefined(Resource2.class));
   }

   @Test
   public void testScopeDeclaredTransitivelyInStereotype()
   {
      assertTrue(Utils.isScopeDefined(Resource3.class));
   }

   @Test
   public void testNoScopeDeclaration()
   {
      assertFalse(Utils.isScopeDefined(Resource4.class));
   }

   @Test
   public void testStereotypeWithoutScopeDeclaration()
   {
      assertFalse(Utils.isScopeDefined(Resource5.class));
   }

   @Test
   public void testImplicitlyDeclaredScopeWithNoScopeStereotype()
   {
      assertTrue(Utils.isScopeDefined(Resource6.class));
   }
}
