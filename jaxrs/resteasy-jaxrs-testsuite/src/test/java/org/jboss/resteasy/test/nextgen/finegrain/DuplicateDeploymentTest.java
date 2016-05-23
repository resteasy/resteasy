package org.jboss.resteasy.test.nextgen.finegrain;

import static io.undertow.servlet.Servlets.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;

import org.jboss.resteasy.plugins.server.servlet.HttpServlet30Dispatcher;
import org.jboss.resteasy.plugins.server.servlet.ServletBootstrap;
import org.jboss.resteasy.plugins.server.servlet.ServletContainerDispatcher;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.ServletInfo;
import junit.framework.Assert;

/**
 * RESTEASY-841
 * 
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @date May 11, 2016
 */
public class DuplicateDeploymentTest
{
   private static UndertowJaxrsServer server;
   private static TestServletContainerDispatcher dispatcher;

   public static class TestUndertowJaxrsServer extends UndertowJaxrsServer
   {
      public DeploymentInfo undertowDeployment(ResteasyDeployment deployment, String mapping)
      {
         if (mapping == null) mapping = "/";
         if (!mapping.startsWith("/")) mapping = "/" + mapping;
         if (!mapping.endsWith("/")) mapping += "/";
         mapping = mapping + "*";
         String prefix = null;
         if (!mapping.equals("/*")) prefix = mapping.substring(0, mapping.length() - 2);
         ServletInfo resteasyServlet = servlet("ResteasyServlet", TestHttpServlet30Dispatcher.class)
               .setAsyncSupported(true)
               .setLoadOnStartup(1)
               .addMapping(mapping);
         if (prefix != null) resteasyServlet.addInitParam("resteasy.servlet.mapping.prefix", prefix);

         return  new DeploymentInfo()
               .addServletContextAttribute(ResteasyDeployment.class.getName(), deployment)
               .addServlet(
                     resteasyServlet
                     );
      } 
   }

   public static class TestHttpServlet30Dispatcher extends HttpServlet30Dispatcher
   {
      private static final long serialVersionUID = 1L;

      public TestHttpServlet30Dispatcher()
      {   
      }

      public void init(ServletConfig servletConfig) throws ServletException
      {
         super.init(servletConfig);
         servletContainerDispatcher = new TestServletContainerDispatcher();
         ServletBootstrap bootstrap = new ServletBootstrap(servletConfig);
         servletContainerDispatcher.init(servletConfig.getServletContext(), bootstrap, this, this);
         servletContainerDispatcher.getDispatcher().getDefaultContextObjects().put(ServletConfig.class, servletConfig);
         dispatcher = (TestServletContainerDispatcher) servletContainerDispatcher;
      }
   }

   public static class TestServletContainerDispatcher extends ServletContainerDispatcher
   {
      ResteasyDeployment getDeployment()
      {
         return deployment;
      }
   }

   @Path("resource")
   public static class TestResource
   {
      @GET
      @Produces("text/plain")
      public String get()
      {
         return "hello world";
      }
   }

   public static class TestReader implements MessageBodyReader<String>
   {
      @Override
      public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
      {
         return false;
      }

      @Override
      public String readFrom(Class<String> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
                  throws IOException, WebApplicationException
      {
         return null;
      }
   }

   @ApplicationPath("/base")
   public static class TestApplication extends Application
   {
      @Override
      public Set<Class<?>> getClasses()
      {
         HashSet<Class<?>> classes = new HashSet<Class<?>>();
         classes.add(TestResource.class);
         classes.add(TestReader.class);
         return classes;
      }

      @Override
      public Set<Object> getSingletons()
      {
         HashSet<Object> singletons = new HashSet<Object>();
         singletons.add(new TestResource());
         singletons.add(new TestReader());
         return singletons;
      }
   }

   @BeforeClass
   public static void init() throws Exception
   {
      server = new TestUndertowJaxrsServer().start();
   }

   @AfterClass
   public static void stop() throws Exception
   {
      server.stop();
   }

   @SuppressWarnings("rawtypes")
   @Test
   public void testDeploy()
   {
      server.deploy(TestApplication.class);
      ResteasyDeployment deployment = dispatcher.getDeployment();
      List<Class> actualResourceClasses = deployment.getActualResourceClasses();
      List<Class> actualProviderClasses = deployment.getActualProviderClasses();
      List<Object> resources = deployment.getResources();
      List<Object> providers = deployment.getProviders();
      Assert.assertTrue(actualResourceClasses.contains(TestResource.class));
      Assert.assertTrue(actualProviderClasses.contains(TestReader.class));
      Assert.assertFalse(resources.contains(TestResource.class));
      Assert.assertFalse(providers.contains(TestReader.class));
   }
}
