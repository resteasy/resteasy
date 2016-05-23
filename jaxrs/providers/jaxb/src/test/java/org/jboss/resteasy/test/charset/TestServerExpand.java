package org.jboss.resteasy.test.charset;

import java.util.Hashtable;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;

/**
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @date Aug 15, 2014
 */
public class TestServerExpand
{
   protected static ResteasyDeployment deployment;
   protected static Dispatcher dispatcher;
   
   public static void main(String[] args)
   {  
      Hashtable<String, String> initParams = new Hashtable<String, String>();
      Hashtable<String, String> contextParams = new Hashtable<String, String>();
      contextParams.put("resteasy.document.expand.entity.references", "false");
      try
      {
         System.out.println("Starting TestServer");
         deployment = EmbeddedContainer.start(initParams, contextParams);
         System.out.println("Started TestServer");
      } catch (Exception e)
      {
         e.printStackTrace();
      }
      dispatcher = deployment.getDispatcher();
      deployment.getRegistry().addPerRequestResource(TestResource.class);
   }
}
