package org.jboss.resteasy.test.util;

import java.lang.reflect.Type;

import javax.ws.rs.ext.ExceptionMapper;
import static org.junit.Assert.assertEquals;

import org.jboss.resteasy.spi.StringConverter;
import org.jboss.resteasy.util.Types;
import org.junit.Test;

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
}
