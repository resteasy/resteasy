package org.jboss.resteasy.test.finegrain.application;

import junit.framework.Assert;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.junit.Test;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ApplicationInjectionTest
{

   public static class MyApplication extends Application
   {
      @Context Application app;

   }

   @Test
   public void testAppInjection()
   {
      ResteasyDeployment deployment = new ResteasyDeployment();
      deployment.setApplicationClass(MyApplication.class.getName());
      deployment.start();
      MyApplication app = (MyApplication)deployment.getApplication();
      Assert.assertNotNull(app.app);
   }
}
