package org.resteasy.plugins.server.servlet;

import org.resteasy.plugins.server.resourcefactory.SingletonResource;
import org.resteasy.spi.Registry;
import org.resteasy.spi.ResteasyProviderFactory;
import org.resteasy.util.GetRestful;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SpringBeanProcessor implements BeanPostProcessor
{
   private Registry registry;
   private ResteasyProviderFactory factory;

   public SpringBeanProcessor(Registry registry, ResteasyProviderFactory factory)
   {
      this.registry = registry;
      this.factory = factory;
   }

   public Object postProcessBeforeInitialization(Object o, String s) throws BeansException
   {
      return o;
   }

   public Object postProcessAfterInitialization(Object bean, String name) throws BeansException
   {
      if (GetRestful.isRestful(bean.getClass()))
      {
         registry.addResourceFactory(new SingletonResource(bean));
      }
      else if (bean.getClass().isAnnotationPresent(Provider.class))
      {
         if (bean instanceof MessageBodyReader)
         {
            factory.addMessageBodyReader((MessageBodyReader) bean);
         }
         if (bean instanceof MessageBodyWriter)
         {
            factory.addMessageBodyWriter((MessageBodyWriter) bean);
         }
      }
      else
      {
      }
      return bean;
   }
}
