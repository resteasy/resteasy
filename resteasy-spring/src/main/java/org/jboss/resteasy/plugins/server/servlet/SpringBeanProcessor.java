package org.jboss.resteasy.plugins.server.servlet;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.plugins.spring.ResteasyRegistration;
import org.jboss.resteasy.plugins.spring.SpringResourceFactory;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.GetRestful;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.StringUtils;

/**
 * <p>
 * This class adds all JAX-RS annotated beans to the RestEasy registry, except
 * for those beans that are referred to by a ResteasyRegistration.
 * </p>
 * 
 * <p>
 * This may inadvertendly cause problems. This registration defers lookup of the
 * object until runtime. The spring object could be wrapped in a proxy, for
 * example, a transactional proxy. The proxy will have to be set with
 * proxy-target-class="true"
 * </p>
 * 
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SpringBeanProcessor implements BeanFactoryPostProcessor
{
   private Registry registry;
   private ResteasyProviderFactory factory;

   public SpringBeanProcessor(Registry registry, ResteasyProviderFactory factory)
   {
      this.registry = registry;
      this.factory = factory;
   }

   public void postProcessBeanFactory(
         final ConfigurableListableBeanFactory beanFactory)
         throws BeansException
   {
      final Collection<String> ignoreBeansList = createIgnoreList(beanFactory);
      beanFactory.addBeanPostProcessor(createBeanPostProcessor(beanFactory,
            ignoreBeansList));
   }

   @SuppressWarnings("unchecked")
   private Collection<String> createIgnoreList(
         final ConfigurableListableBeanFactory beanFactory)
   {
      Map<String, ResteasyRegistration> registries = beanFactory
            .getBeansOfType(ResteasyRegistration.class);

      final Collection<String> ignoreBeansList = new HashSet<String>();
      for (ResteasyRegistration registration : registries.values())
      {
         String beanName = registration.getBeanName();
         ignoreBeansList.add(beanName);
         registerResource(beanFactory, beanName, registration.getContext());

      }
      return ignoreBeansList;
   }

   private void registerResource(
         final ConfigurableListableBeanFactory beanFactory, String beanName,
         String basePath)
   {
      SpringResourceFactory resourceFactory = new SpringResourceFactory(
            beanName);
      resourceFactory.setBeanFactory(beanFactory);
      if (StringUtils.hasText(basePath))
         registry.addResourceFactory(resourceFactory, basePath);
      else
         registry.addResourceFactory(resourceFactory);
   }

   protected BeanPostProcessor createBeanPostProcessor(
         final ConfigurableListableBeanFactory beanFactory,
         final Collection<String> ignoreBeansList)
   {
      return new BeanPostProcessor()
      {
         public Object postProcessAfterInitialization(Object bean,
               String beanName) throws BeansException
         {
            if (GetRestful.isRootResource(bean.getClass())
                  && !ignoreBeansList.contains(beanName))
            {
               registerResource(beanFactory, beanName, null);
            }

            if (bean.getClass().isAnnotationPresent(Provider.class))
            {
               factory.registerProviderInstance(bean);
            }
            return bean;
         }

         public Object postProcessBeforeInitialization(Object bean,
               String beanName) throws BeansException
         {
            return bean;
         }
      };
   }
}
