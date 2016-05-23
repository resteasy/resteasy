package org.jboss.resteasy.test.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.List;

import javax.ws.rs.core.GenericType;

import org.jboss.resteasy.util.Types;
import org.junit.Test;

import junit.framework.Assert;

/**
 * RESTEASY-1295
 * 
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @date
 */
public class WildcardTypeTest
{
   @Test
   public void testWildcardTypeUpperBound()
   {
      GenericType<List<? extends String>> genericType= new GenericType<List<? extends String>>() {};
      ParameterizedType pt = (ParameterizedType) genericType.getType();
      Type t = pt.getActualTypeArguments()[0];
//      printTypes(t);
      Class<?> rawType = Types.getRawType(t);
      System.out.println(rawType);
      Assert.assertEquals(String.class, rawType);
   }
   
   @Test
   public void testWildcardTypeLowerBound()
   {
      GenericType<List<? super String>> genericType= new GenericType<List<? super String>>() {};
      ParameterizedType pt = (ParameterizedType) genericType.getType();
      Type t = pt.getActualTypeArguments()[0];
//      printTypes(t);
      Class<?> rawType = Types.getRawType(t);
      System.out.println(rawType);
      Assert.assertEquals(Object.class, rawType);
   }
   
   @Test
   public void testWildcardTypeUpperBoundObject()
   {
      GenericType<List<? extends Object>> genericType= new GenericType<List<? extends Object>>() {};
      ParameterizedType pt = (ParameterizedType) genericType.getType();
      Type t = pt.getActualTypeArguments()[0];
//      printTypes(t);
      Class<?> rawType = Types.getRawType(t);
      System.out.println(rawType);
      Assert.assertEquals(Object.class, rawType);
   }
   
   @Test
   public void testWildcardTypeLowerBoundObject()
   {
      GenericType<List<? super Object>> genericType= new GenericType<List<? super Object>>() {};
      ParameterizedType pt = (ParameterizedType) genericType.getType();
      Type t = pt.getActualTypeArguments()[0];
//      printTypes(t);
      Class<?> rawType = Types.getRawType(t);
      System.out.println(rawType);
      Assert.assertEquals(Object.class, rawType);
   }
   
   void printTypes(Type t)
   {
      WildcardType wt = (WildcardType)t;
      System.out.println("\rtype:  " + t);
      System.out.println("upper: " + (wt.getUpperBounds().length > 0 ? wt.getUpperBounds()[0] : "[]"));
      System.out.println("lower: " + (wt.getLowerBounds().length > 0 ? wt.getLowerBounds()[0] : "[]"));
   }
}
