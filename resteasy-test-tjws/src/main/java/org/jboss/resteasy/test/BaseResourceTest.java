/**
 *
 */
package org.jboss.resteasy.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;

import javax.ws.rs.ext.ExceptionMapper;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.After;
import org.junit.Before;

/**
 * @author <a href="mailto:ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
public abstract class BaseResourceTest
{
   protected boolean manualStart;

   protected Hashtable<String,String> initParams = new Hashtable<String,String>();
   protected Hashtable<String,String> contextParams = new Hashtable<String,String>();
   protected ResteasyDeployment deployment;
   protected Dispatcher dispatcher;

   @Before
   public void before() throws Exception {
       if (!manualStart)
           startContainer();
   }

   protected void createContainer(Hashtable<String,String> initParams, Hashtable<String, String> contextParams) throws Exception {
     this.initParams = initParams;
     this.contextParams = contextParams;
     startContainer();
   }

   protected void startContainer() throws Exception {
     if (deployment == null) {
         deployment = EmbeddedContainer.start(initParams, contextParams);
         dispatcher = deployment.getDispatcher();
     }
   }

   protected void startContainerIfNotRunning() {
       try {
           startContainer();
       } catch (Exception e) {
           throw new IllegalStateException("Can not start TJWS container " + e.getMessage(), e);
       }
   }

   @After
   public void after() throws Exception
   {
      stopContainer();
   }

   protected void stopContainer() throws Exception {
      if (deployment != null)
        EmbeddedContainer.stop();
      dispatcher = null;
      deployment = null;
   }

   public Registry getRegistry()
   {
      startContainerIfNotRunning();
      return deployment.getRegistry();
   }

   public ResteasyProviderFactory getProviderFactory()
   {
      startContainerIfNotRunning();
      return deployment.getProviderFactory();
   }

   public void addPerRequestResource(Class<?> resource, Class<?> ... otherResources)
   {
      startContainerIfNotRunning();
      deployment.getRegistry().addPerRequestResource(resource);
   }

   protected void addPackageInfo(final Class<?> clazz) {
      // required for arquillian
   }

   public void registerProvider(Class<?> clazz) {
      startContainerIfNotRunning();
      deployment.getProviderFactory().registerProvider(clazz);
   }

   public void addExceptionMapper(Class<? extends ExceptionMapper<?>> clazz) {
      startContainerIfNotRunning();
      deployment.getProviderFactory().addExceptionMapper(clazz);
   }

   public String readString(InputStream in) throws IOException
   {
      char[] buffer = new char[1024];
      StringBuilder builder = new StringBuilder();
      BufferedReader reader = new BufferedReader(new InputStreamReader(in));
      int wasRead = 0;
      do
      {
         wasRead = reader.read(buffer, 0, 1024);
         if (wasRead > 0)
         {
            builder.append(buffer, 0, wasRead);
         }
      }
      while (wasRead > -1);

      return builder.toString();
   }
}