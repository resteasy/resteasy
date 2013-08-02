package org.jboss.resteasy.cdi.test.intf.ejb;

import org.jboss.resteasy.cdi.ResteasyCdiExtension;
import org.junit.Before;
import org.junit.Test;

import javax.enterprise.inject.spi.Bean;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertTrue;

public class SessionBeanInterfaceTest
{
   private ResteasyCdiExtension extension;
   
   @Before
   public void prepare()
   {
      extension = new ResteasyCdiExtension();
   }
   
   @Test
   public void testJaxrsAnnotatedInterfaceSelected()
   {
      Set<Type> types = new HashSet<Type>();
      types.add(FooLocal.class);
      types.add(FooLocal2.class);
      types.add(FooLocal3.class);
      types.add(Object.class);
      Bean<Object> bean = new MockBean<Object>(Foo.class, types);
      extension.observeSessionBeans(new MockProcessSessionBean<Foo>(bean));
      assertTrue(extension.getSessionBeanInterface().get(Foo.class).equals(FooLocal3.class));
   }
   
   @Test
   public void testNoInterfaceSelected()
   {
      Set<Type> types = new HashSet<Type>();
      types.add(Foo.class);
      types.add(Object.class);
      Bean<Object> bean = new MockBean<Object>(Foo.class, types);
      extension.observeSessionBeans(new MockProcessSessionBean<Foo>(bean));
      assertTrue(extension.getSessionBeanInterface().isEmpty());
   }
}
