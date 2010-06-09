package org.jboss.resteasy.test.interfaced;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import static org.jboss.resteasy.test.TestPortProvider.*;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Application;
import javax.ws.rs.Path;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Magesh Kumar B
 * @version $Revision: 1 $
 */
public class InterfacedTest
{

   public static class EchoService implements EchoBase
   {
       public String echo(String message)
       {
          System.out.println(message);
          return "echo " + message;
       }
   }

   public static class MyApplicationConfig extends Application
   {
      private Set<Class<?>> classes = new HashSet<Class<?>>();

      public MyApplicationConfig()
      {
         classes.add(EchoService.class);
      }

      @Override
      public Set<Class<?>> getClasses()
      {
         return classes;
      }

   }

   @BeforeClass
   public static void before() throws Exception
   {
      ResteasyDeployment deployment = new ResteasyDeployment();
      deployment.setApplication(new MyApplicationConfig());
      EmbeddedContainer.start(deployment);
   }

   @AfterClass
   public static void after() throws Exception
   {
      EmbeddedContainer.stop();
   }

   @Test
   public void testEcho() throws Exception
   {
      HttpClient client = new HttpClient();
      {
         GetMethod method = new GetMethod(generateURL("/Hello"));
         int status = client.executeMethod(method);
         Assert.assertEquals(HttpServletResponse.SC_OK, status);
         Assert.assertEquals("echo Hello", method.getResponseBodyAsString());

         method.releaseConnection();
      }
   }

}
