package org.jboss.resteasy.cdi.test.scopes;

import org.jboss.resteasy.cdi.Bootstrap;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertFalse;

public class ScopeDetectionTest
{
   private Bootstrap bootstrap;

   @BeforeMethod
   public void prepare()
   {
      bootstrap = new Bootstrap();
   }
   
   @Test
   public void testImplicitlyDeclaredScope()
   {
      assertTrue(bootstrap.hasScopeDefined(Resource1.class));
   }

   @Test
   public void testScopeDeclaredInStereotype()
   {
      assertTrue(bootstrap.hasScopeDefined(Resource2.class));
   }

   @Test
   public void testScopeDeclaredTransitivelyInStereotype()
   {
      assertTrue(bootstrap.hasScopeDefined(Resource3.class));
   }

   @Test
   public void testNoScopeDeclaration()
   {
      assertFalse(bootstrap.hasScopeDefined(Resource4.class));
   }

   @Test
   public void testStereotypeWithoutScopeDeclaration()
   {
      assertFalse(bootstrap.hasScopeDefined(Resource5.class));
   }

   @Test
   public void testImplicitlyDeclaredScopeWithNoScopeStereotype()
   {
      assertTrue(bootstrap.hasScopeDefined(Resource6.class));
   }

}
