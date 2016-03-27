package org.jboss.resteasy.test.util;

import org.jboss.resteasy.spi.StringConverter;
import org.jboss.resteasy.util.Types;
import org.junit.Test;

import javax.ws.rs.ext.ExceptionMapper;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TypesTest
{

   @Test
   public void testGetInterfaceArgumentFromSimpleType()
   {
      Type[] parameters = Types.getActualTypeArgumentsOfAnInterface(SimpleProvider.class, ExceptionMapper.class);
      assertEquals(1, parameters.length);
      assertEquals(NullPointerException.class, (Class<?>) parameters[0]);

      parameters = Types.getActualTypeArgumentsOfAnInterface(SimpleProvider.class, StringConverter.class);
      assertEquals(1, parameters.length);
      assertEquals(Integer.class, (Class<?>) parameters[0]);
   }

   @Test
   // Provider subclasses are not defined by the spec, but we need to be able to recognize them for proxied providers to be identified.
   public void testGetInterfaceArgumentFromSubclass()
   {
      Type[] parameters = Types.getActualTypeArgumentsOfAnInterface(SimpleProviderSubclass.class, ExceptionMapper.class);
      assertEquals(1, parameters.length);
      assertEquals(NullPointerException.class, (Class<?>) parameters[0]);

      parameters = Types.getActualTypeArgumentsOfAnInterface(SimpleProviderSubclass.class, StringConverter.class);
      assertEquals(1, parameters.length);
      assertEquals(Integer.class, (Class<?>) parameters[0]);
   }

   @Test
   public void testGenericWildcardTypesSupported() throws Exception
   {
      List<List<? extends Number>> numberLists = new ArrayList<List<? extends Number>>() {};
      ParameterizedType arrayListType = (ParameterizedType) numberLists.getClass().getGenericSuperclass();
      ParameterizedType listType = (ParameterizedType) arrayListType.getActualTypeArguments()[0];
      WildcardType wildcardType = (WildcardType) listType.getActualTypeArguments()[0];

      assertEquals(Number.class, Types.getRawType(wildcardType));

   }
}
