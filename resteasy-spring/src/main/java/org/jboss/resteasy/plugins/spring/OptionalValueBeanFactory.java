package org.jboss.resteasy.plugins.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;

/**
 * This resovles optional
 * 
 * @author <a href="mailto:sduskis@gmail.com">Solomon Duskis</a>
 * @version $Revision: 1 $
 */

public class OptionalValueBeanFactory implements FactoryBean, BeanFactoryAware
{
   private final static Logger logger = LoggerFactory
         .getLogger(OptionalValueBeanFactory.class);

   private String beanName;
   private Class<?> clazz;
   private BeanFactory beanFactory;

   public String getBeanName()
   {
      return beanName;
   }

   public void setBeanName(String beanName)
   {
      this.beanName = beanName;
   }

   public Class<?> getClazz()
   {
      return clazz;
   }

   public void setClazz(Class<?> clazz)
   {
      this.clazz = clazz;
   }

   public Object getObject() throws Exception
   {
      try
      {
         if (beanFactory.containsBean(beanName))
            return beanFactory.getBean(beanName, clazz);
      }
      catch (Exception e)
      {
         logger.error("Could not retrieve bean " + beanName, e);
      }
      return null;
   }

   public Class<?> getObjectType()
   {
      return clazz;
   }

   public boolean isSingleton()
   {
      return true;
   }

   public void setBeanFactory(BeanFactory beanFactory) throws BeansException
   {
      this.beanFactory = beanFactory;
   }

}
