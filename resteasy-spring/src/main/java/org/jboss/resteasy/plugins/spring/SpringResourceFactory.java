package org.jboss.resteasy.plugins.spring;

import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.InjectorFactory;
import org.jboss.resteasy.spi.PropertyInjector;
import org.jboss.resteasy.spi.ResourceFactory;
import org.springframework.beans.factory.BeanFactory;

/**
* 
* 
* @author <a href="mailto:sduskis@gmail.com">Solomn Duskis</a>
* @version $Revision: 1 $
*/

public class SpringResourceFactory implements ResourceFactory 
{

   protected BeanFactory beanFactory;
   protected String beanName;
   protected Class<?> scannableClass;
   protected PropertyInjector propertyInjector;
   protected String context = null;

   public SpringResourceFactory(String beanName, BeanFactory beanFactory, Class<?> scannable)
   {
      this.beanName = beanName;
      this.beanFactory = beanFactory;
      this.scannableClass = scannable;
   }

   public PropertyInjector getPropertyInjector()
   {
      return propertyInjector;
   }

   public Object createResource(HttpRequest request, HttpResponse response,
                                InjectorFactory factory)
   {
      return beanFactory.getBean(beanName);
   }

   public Class<?> getScannableClass()
   {
      return scannableClass;
   }

   public void registered(InjectorFactory factory)
   {
      this.propertyInjector = factory.createPropertyInjector(getScannableClass());
   }

   public void requestFinished(HttpRequest request, HttpResponse response,
                               Object resource)
   {
   }

   public void unregistered()
   {
   }
   
   public String getBeanName()
   {
      return beanName;
   }
   
   public void setContext(String context)
   {
      this.context = context;
   }
   
   public String getContext()
   {
      return context;
   }
}
