package org.jboss.resteasy.plugins.spring;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.MessageBodyWorkers;
import javax.ws.rs.ext.Providers;

import org.jboss.resteasy.core.ContextParameterInjector;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.Ordered;

/**
 * 
 * @author <a href="mailto:sduskis@gmail.com">Solomn Duskis</a>
 * @version $Revision: 1 $
 */

public class ResteasyContextFactoryBean implements BeanFactoryPostProcessor,
      Ordered
{

   private static List<Class<?>> DEFAULT_OBJECT_TYPES = Arrays
         .<Class<?>> asList(UriInfo.class, HttpHeaders.class, Request.class,
               MessageBodyWorkers.class, Providers.class, HttpRequest.class);

   private List<Class<?>> objectTypes = DEFAULT_OBJECT_TYPES;

   private int order = 0;
   private ResteasyProviderFactory factory = ResteasyProviderFactory
         .getInstance();

   @Required
   public List<Class<?>> getObjectTypes()
   {
      return objectTypes;
   }

   public void setObjectTypes(List<Class<?>> objectTypes)
   {
      this.objectTypes = objectTypes;
   }

   public ResteasyProviderFactory getFactory()
   {
      return factory;
   }

   public void setFactory(ResteasyProviderFactory factory)
   {
      this.factory = factory;
   }

   public void postProcessBeanFactory(
         ConfigurableListableBeanFactory beanFactory) throws BeansException
   {
      for (final Class<?> clazz : objectTypes)
      {
         try
         {
            beanFactory.registerResolvableDependency(clazz, new ObjectFactory()
            {
               public Object getObject() throws BeansException
               {
                  return new ContextParameterInjector(clazz, factory).inject();
               }
            });
         }
         catch (Throwable e)
         {
            e.printStackTrace();
         }
      }
   }

   public int getOrder()
   {
      return order;
   }

   public void setOrder(int order)
   {
      this.order = order;
   }
}
