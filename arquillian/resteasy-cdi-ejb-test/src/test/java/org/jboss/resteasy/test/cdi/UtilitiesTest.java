package org.jboss.resteasy.test.cdi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.util.AnnotationLiteral;

import junit.framework.Assert;

import org.jboss.resteasy.cdi.extension.bean.Boston;
import org.jboss.resteasy.cdi.extension.bean.BostonHolder;
import org.jboss.resteasy.cdi.extension.bean.BostonlLeaf;
import org.jboss.resteasy.cdi.extension.bean.TestResource;
import org.jboss.resteasy.cdi.injection.BookResource;
import org.jboss.resteasy.cdi.util.Utilities;
import org.junit.Test;

public class UtilitiesTest
{  
   private static Logger log = Logger.getLogger(UtilitiesTest.class.getName());
 
   @RequestScoped
   @Boston
   static class C
   {     
   }
   
   @Test
   public void testGetQualifiers()
   {
      log.info("entering testGetQualifiers()");
      Set<Annotation> qualifiers = Utilities.getQualifiers(C.class);
      System.out.println(qualifiers);
      Assert.assertEquals(1, qualifiers.size());
      HashSet<Class<?>> qualifierClasses = new HashSet<Class<?>>();
      Iterator<Annotation> it = qualifiers.iterator();
      while (it.hasNext())
      {
         Annotation a = it.next();
         System.out.println("a type: " + a.annotationType());
         qualifierClasses.add(a.annotationType());
      }
      Assert.assertTrue(qualifierClasses.contains(Boston.class));
      log.info("testAnnotation() PASSES");
   }
   
   @Test
   public void testHasQualifier()
   {
      log.info("entering testHasQualifier()");
      Assert.assertTrue(Utilities.hasQualifier(BookResource.class, RequestScoped.class));
      Assert.assertTrue(Utilities.isBoston(BostonHolder.class));
      Assert.assertTrue(Utilities.isBoston(BostonlLeaf.class));
      Assert.assertFalse(Utilities.isBoston(TestResource.class));
   }
   
   interface i1 {}
   interface i2 extends i1 {}
   static class c1 implements i1 {}
   static class c2 extends c1 implements i2 {}
   
   @Test
   public void testTypeClosure() throws Exception
   {
      log.info("entering testTypeClosure()");
      Set<Type> set = Utilities.getTypeClosure(c2.class);
      System.out.println(set);
      Assert.assertEquals(5, set.size());
      Assert.assertTrue(set.contains(i1.class));
      Assert.assertTrue(set.contains(i2.class));
      Assert.assertTrue(set.contains(c1.class));
      Assert.assertTrue(set.contains(c2.class));
      Assert.assertTrue(set.contains(Object.class));
   }
   
   @SuppressWarnings("serial")
   @Test
   public void testAnnotationPresent() throws Exception
   {
      Assert.assertTrue(Utilities.isAnnotationPresent(C.class, new AnnotationLiteral<Boston>(){}.annotationType()));
      Assert.assertTrue(Utilities.isAnnotationPresent(C.class, new AnnotationLiteral<RequestScoped>(){}.annotationType()));
      Assert.assertFalse(Utilities.isAnnotationPresent(C.class, new AnnotationLiteral<SessionScoped>(){}.annotationType()));
   }
}

