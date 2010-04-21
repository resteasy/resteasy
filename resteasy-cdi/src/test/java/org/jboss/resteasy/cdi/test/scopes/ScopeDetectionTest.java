package org.jboss.resteasy.cdi.test.scopes;

import org.jboss.resteasy.cdi.ResteasyCdiExtension;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;


public class ScopeDetectionTest
{
   private ResteasyCdiExtension bootstrap;

   @Before
   public void prepare()
   {
      bootstrap = new ResteasyCdiExtension();
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
