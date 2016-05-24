package org.jboss.resteasy.plugins.spring;

import java.util.Collection;

public class BeanNameFilter implements ResourceRegistrationFilter
{

   private Collection<String> beanNames;

   public Collection<String> getBeanNames()
   {
      return beanNames;
   }

   public void setBeanNames(Collection<String> beanNames)
   {
      this.beanNames = beanNames;
   }

   public boolean include(String beanName, Object object)
   {
      return beanName.contains(beanName);
   }

}
