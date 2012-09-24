package org.jboss.resteasy.plugins.spring;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.core.ResourceMethodRegistry;
import org.jboss.resteasy.mock.MockDispatcherFactory;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.support.StaticWebApplicationContext;

import javax.servlet.ServletContext;

import static org.junit.Assert.assertEquals;

/**
 * Tests that SpringContextLoader does proper validations and adds an application listener.
 * This was used to extract the code into SpringContextLoaderSupport, so that it could be re-used
 * without having to extend ContextLoader.  Probably should move these tests to directly
 * test SpringContextLoaderSupport and replace this test with a test that simply asserts
 * that SpringContextLoaderSupport was callled.
 */
public class TestSpringContextLoader
{

   private SpringContextLoader contextLoader;

   @Before
   public void setupEditor()
   {
      contextLoader = new SpringContextLoader();
   }

   @Test(expected=RuntimeException.class)
   public void testThatProviderFactoryIsRequired() 
   {
      contextLoader.customizeContext(
            mockServletContext(null,someRegistry(),someDispatcher()),
            mockWebApplicationContext());
   }

   @Test(expected=RuntimeException.class)
   public void testThatRegistryIsRequired() 
   {
      contextLoader.customizeContext(
            mockServletContext(someProviderFactory(),null,someDispatcher()),
            mockWebApplicationContext());
   }

   @Test(expected=RuntimeException.class)
   public void testThatDispatcherIsRequired() 
   {
      contextLoader.customizeContext(
            mockServletContext(someProviderFactory(),someRegistry(),null),
            mockWebApplicationContext());
   }

   @Test
   public void testThatWeAddedAnApplicationListener() 
   {
      StaticWebApplicationContext context = mockWebApplicationContext();
      int numListeners = context.getApplicationListeners().size();
      contextLoader.customizeContext(
            mockServletContext(someProviderFactory(),someRegistry(),someDispatcher()),
            context);
      int numListenersNow = context.getApplicationListeners().size();
      assertEquals("Expected to add exactly one new listener; in fact added " + (numListenersNow - numListeners),
         numListeners + 1,numListenersNow);
   }

   private StaticWebApplicationContext mockWebApplicationContext() 
   {
      return new StaticWebApplicationContext();
   }

   private ServletContext mockServletContext(
           ResteasyProviderFactory providerFactory,
           Registry registry,
           Dispatcher dispatcher) 
   {
      MockServletContext context = new MockServletContext();

      if (providerFactory != null)
         context.setAttribute(ResteasyProviderFactory.class.getName(),providerFactory);

      if (registry != null) 
         context.setAttribute(Registry.class.getName(),registry);

      if (dispatcher != null)
         context.setAttribute(Dispatcher.class.getName(),dispatcher);

      return context;
   }

   private Registry someRegistry() 
   {
      return new ResourceMethodRegistry(someProviderFactory());
   }

   private ResteasyProviderFactory someProviderFactory() 
   {
      return new ResteasyProviderFactory();
   }

   private Dispatcher someDispatcher() 
   {
      return MockDispatcherFactory.createDispatcher();
   }
}
