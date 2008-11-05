package org.jboss.resteasy.plugin.server.servlet;

import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.GetRestful;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

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
      if (GetRestful.isRootResource(bean.getClass()))
      {
         registry.addSingletonResource(bean);
      }
      else if (bean.getClass().isAnnotationPresent(Provider.class))
      {
         factory.registerProviderInstance(bean);
      }
      else
      {
      }
      return bean;
   }
}
