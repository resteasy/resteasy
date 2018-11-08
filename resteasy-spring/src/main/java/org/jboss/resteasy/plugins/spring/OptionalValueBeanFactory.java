package org.jboss.resteasy.plugins.spring;

import org.jboss.resteasy.plugins.spring.i18n.LogMessages;
import org.jboss.resteasy.plugins.spring.i18n.Messages;
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

public class OptionalValueBeanFactory implements FactoryBean<Object>, BeanFactoryAware
{

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
         LogMessages.LOGGER.error(Messages.MESSAGES.couldNotRetrieveBean(beanName), e);
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
