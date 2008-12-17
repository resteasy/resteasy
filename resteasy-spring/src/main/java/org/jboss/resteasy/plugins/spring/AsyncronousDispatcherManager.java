package org.jboss.resteasy.plugins.spring;

import org.jboss.resteasy.core.AsynchronousDispatcher;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class AsyncronousDispatcherManager implements InitializingBean,
      DisposableBean
{

   AsynchronousDispatcher dispatcher;

   public AsynchronousDispatcher getDispatcher()
   {
      return dispatcher;
   }

   public void setDispatcher(AsynchronousDispatcher dispatcher)
   {
      this.dispatcher = dispatcher;
   }

   public void afterPropertiesSet() throws Exception
   {
      dispatcher.start();
   }

   public void destroy() throws Exception
   {
      dispatcher.stop();
   }

}
