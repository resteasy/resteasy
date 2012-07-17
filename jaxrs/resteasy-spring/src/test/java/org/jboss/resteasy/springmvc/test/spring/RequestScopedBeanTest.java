package org.jboss.resteasy.springmvc.test.spring;

import junit.framework.Assert;
import org.jboss.resteasy.core.InjectorFactoryImpl;
import org.jboss.resteasy.core.ValueInjector;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.springmvc.tjws.TJWSEmbeddedSpringMVCServer;
import org.jboss.resteasy.test.TestPortProvider;
import org.jboss.resteasy.util.FindAnnotation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Type;

import static org.jboss.resteasy.test.TestPortProvider.*;

public class RequestScopedBeanTest
{
   @Provider
   public static class QualifierInjectorFactoryImpl extends InjectorFactoryImpl implements
         BeanFactoryAware
   {
      BeanFactory beanFactory;

      public QualifierInjectorFactoryImpl(ResteasyProviderFactory factory)
      {
         super(factory);
      }

      @SuppressWarnings("rawtypes")
      @Override
      public ValueInjector createParameterExtractor(Class injectTargetClass,
            AccessibleObject injectTarget, Class type, Type genericType, Annotation[] annotations)
      {
         final Qualifier qualifier = FindAnnotation.findAnnotation(annotations, Qualifier.class);
         if (qualifier == null)
         {
            return super.createParameterExtractor(injectTargetClass, injectTarget, type,
                  genericType, annotations);
         }
         else
         {
            return new ValueInjector()
            {
               public Object inject(HttpRequest request, HttpResponse response)
               {
                  return beanFactory.getBean(qualifier.value());
               }

               public Object inject()
               {
                  // do nothing.
                  return null;
               }
            };
         }
      }

      @Override
      public void setBeanFactory(BeanFactory beanFactory) throws BeansException
      {
         this.beanFactory = beanFactory;
      }
   }

   public static class TestBean
   {
      String configured;

      public void setConfigured(String configured)
      {
         this.configured = configured;
      }
   }

   @Path("/")
   public static class TestBeanResource
   {
      @GET
      public String test(@Qualifier("testBean") TestBean bean)
      {
         return bean.configured;
      }
   }

   private TJWSEmbeddedSpringMVCServer server;

   @Before
   public void startServer()
   {
      server = new TJWSEmbeddedSpringMVCServer("classpath:spring-request-scope-test-server.xml",
            TestPortProvider.getPort());
      server.start();
   }

   @After
   public void stopServer()
   {
      server.stop();
   }

   @Test
   public void testBean() throws Exception
   {
      String result = createClientRequest("/").accept(MediaType.TEXT_PLAIN_TYPE).get(String.class)
            .getEntity();
      Assert.assertEquals("configuredValue", result);
   }

}
