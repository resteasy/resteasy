package org.jboss.resteasy.test.spring.deployment.resource;

import org.springframework.beans.factory.FactoryBean;

public class SpringBeanProcessorMyBeanFactoryBean implements FactoryBean<SpringBeanProcessorMyBean> {

   @Override
   public SpringBeanProcessorMyBean getObject() throws Exception {
      return new SpringBeanProcessorMyBean();
   }

   @Override
   public Class<?> getObjectType() {
      return SpringBeanProcessorMyBean.class;
   }

   @Override
   public boolean isSingleton() {
      return true;
   }
}
