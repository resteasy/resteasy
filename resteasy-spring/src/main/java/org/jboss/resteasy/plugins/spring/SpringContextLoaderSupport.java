package org.jboss.resteasy.plugins.spring;

import org.jboss.resteasy.plugins.spring.i18n.Messages;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.springframework.web.context.ConfigurableWebApplicationContext;

import jakarta.servlet.ServletContext;

/**
 * Provides access to RESTEasy's SpringContextLoader implementation without having
 * to extend {@link org.springframework.web.context.ContextLoader}.  This is useful
 * if you have your own SpringContextLoaderListener and dont' want to
 * return RESTEasy's {@link SpringContextLoader}.
 *
 * Usage:
 * <pre>
 * public class MyCustomSpringContextLoader extends ContextLoader
 * {
 *    private SpringContextLoaderSupport springContextLoaderSupport =
 *       new SpringContextLoaderSupport();
 *
 *     protected void customizeContext(
 *        ServletContext servletContext,
 *        ConfigurableWebApplicationContext configurableWebApplicationContext)
 *    {
 *       super.customizeContext(servletContext, configurableWebApplicationContext);
 *
 *       // Your custom code
 *
 *       this.springContextLoaderSupport.customizeContext(servletContext.configurableWebApplicationContext);
 *
 *       // Your custom code
 *    }
 * }
 * </pre>
 */
public class SpringContextLoaderSupport
{
   public void customizeContext(ServletContext servletContext, ConfigurableWebApplicationContext configurableWebApplicationContext)
   {
      SpringBeanProcessor processor = null;

      ResteasyDeployment deployment = (ResteasyDeployment) servletContext.getAttribute(ResteasyDeployment.class.getName());
      if (deployment == null) {
         throw new RuntimeException(Messages.MESSAGES.deploymentIsNull());
      }

      processor = new SpringBeanProcessor(deployment);
      configurableWebApplicationContext.addBeanFactoryPostProcessor(processor);
      configurableWebApplicationContext.addApplicationListener(processor);
   }
}
