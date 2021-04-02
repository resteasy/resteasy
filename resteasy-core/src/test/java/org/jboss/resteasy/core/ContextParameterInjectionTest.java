package org.jboss.resteasy.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;

import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.Test;
import org.junit.AfterClass;

public class ContextParameterInjectionTest {

   @AfterClass
   public static void cleanup() {
      ResteasyProviderFactory.setInstance(null);
   }

   @Test
   public void testInjectedProxyImplementsAllInterfaces() {
      final Class<ContainerRequestFilter> filterClass = ContainerRequestFilter.class;
      final ContainerRequestFilter coolInstance = new CoolFilter();
      ResteasyProviderFactory mockFactory = mock(ResteasyProviderFactory.class);
      when(mockFactory.getContextData(filterClass, filterClass, null, false)).thenReturn(coolInstance);

      ResteasyProviderFactory.setInstance(mockFactory);

      ContextParameterInjector cpi = new ContextParameterInjector(null, filterClass, filterClass, null, mockFactory);
      Object proxy = cpi.createProxy();
      assertTrue("Proxy does not implement all expected interfaces", proxy instanceof CoolInterface);
      assertEquals("cool", ((CoolInterface)proxy).coolMethod());
   }

   public interface CoolInterface {
      String coolMethod();
   }

   public class CoolFilter implements ContainerRequestFilter, CoolInterface {

      @Override
      public String coolMethod()
      {
         return "cool";
      }

      @Override
      public void filter(ContainerRequestContext requestContext) throws IOException
      {
         // do some cool filtering
      }
   }
}
