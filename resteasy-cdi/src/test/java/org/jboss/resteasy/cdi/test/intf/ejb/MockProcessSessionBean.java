package org.jboss.resteasy.cdi.test.intf.ejb;

import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.ProcessSessionBean;
import javax.enterprise.inject.spi.SessionBeanType;

public class MockProcessSessionBean<T> implements ProcessSessionBean<T>
{
   
   private Bean<Object> bean;
   
   public MockProcessSessionBean(Bean<Object> bean)
   {
      this.bean = bean;
   }


   public Bean<Object> getBean()
   {
      return bean;
   }
   

   public String getEjbName()
   {
      return null;
   }

   public SessionBeanType getSessionBeanType()
   {
      return null;
   }

   public AnnotatedType<Object> getAnnotatedBeanClass()
   {
      return null;
   }

   public void addDefinitionError(Throwable t)
   {
   }

   public Annotated getAnnotated()
   {
      return null;
   }
}
