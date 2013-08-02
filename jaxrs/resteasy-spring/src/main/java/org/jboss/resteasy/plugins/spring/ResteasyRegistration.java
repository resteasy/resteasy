package org.jboss.resteasy.plugins.spring;

import org.springframework.beans.factory.annotation.Required;

/**
 * @author <a href="mailto:sduskis@gmail.com">Solomn Duskis</a>
 * @version $Revision: 1 $
 */

public class ResteasyRegistration
{

   private String context = "";
   private String beanName;

   public ResteasyRegistration()
   {
   }

   public ResteasyRegistration(String context, String beanName)
   {
      this.context = context;
      this.beanName = beanName;
   }

   @Required
   public String getContext()
   {
      return context;
   }

   public void setContext(String context)
   {
      this.context = context;
   }

   @Required
   public String getBeanName()
   {
      return this.beanName;
   }

   public void setBeanName(String beanName)
   {
      this.beanName = beanName;
   }

}
