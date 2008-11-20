package org.jboss.resteasy.plugins.server.servlet;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.plugins.spring.ResteasyRegistration;
import org.jboss.resteasy.plugins.spring.SpringResourceFactory;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.GetRestful;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

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
   private Logger logger = LoggerFactory.getLogger(SpringBeanProcessor.class);
   private Registry registry;
   private ResteasyProviderFactory factory;
   private String context;
   private ResourceRegistrationFilter filter;

   public SpringBeanProcessor(Registry registry, ResteasyProviderFactory factory)
   {
      this.registry = registry;
      this.factory = factory;
   }

   public SpringBeanProcessor(Registry registry,
         ResteasyProviderFactory factory, String context,
         ResourceRegistrationFilter filter)
   {
      super();
      this.registry = registry;
      this.factory = factory;
      this.context = context;
      this.filter = filter;
   }

   public void postProcessBeanFactory(
         final ConfigurableListableBeanFactory beanFactory)
         throws BeansException
   {
      final Collection<String> ignoreBeansList = createIgnoreList(beanFactory);
      beanFactory.addBeanPostProcessor(createBeanPostProcessor(beanFactory,
            ignoreBeansList));
   }

   private Collection<String> createIgnoreList(
         final ConfigurableListableBeanFactory beanFactory)
   {
      return new HashSet<String>(Arrays.asList(beanFactory.getBeanNamesForType(
            ResteasyRegistration.class, true, true)));
   }

   private void registerResource(
         final ConfigurableListableBeanFactory beanFactory, String beanName,
         String basePath)
   {
      logger.info("registering bean " + beanName + " via SpringBeanProcessor");
      SpringResourceFactory resourceFactory = new SpringResourceFactory(beanName);
      resourceFactory.setBeanFactory(beanFactory);
      registry.addResourceFactory(resourceFactory, basePath);
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
                  && !ignoreBeansList.contains(beanName)
                  && (filter == null || filter.include(beanName, bean)))
            {
               registerResource(beanFactory, beanName, context);
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
