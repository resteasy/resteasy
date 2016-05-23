package org.jboss.resteasy.test.nextgen.providers;

import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;

import org.junit.Test;

import junit.framework.Assert;

/**
 * RESTEASY-1284
 *  
 * @author Nicolas NESMON
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * 
 * @date April 7, 2016
 */
public class DuplicateProviderRegistrationTest
{
   protected static Process process;
   
   public static void main(String[] args)
   {
      DuplicateProviderRegistrationTest test = new DuplicateProviderRegistrationTest();
      test.testDuplicateProvider();
      test.testFromJavadoc();
   }
   
   public static void deployJVM() throws InterruptedException
   {
      String separator = System.getProperty("file.separator");
      String classpath = System.getProperty("java.class.path");
      String java = System.getProperty("java.home") + separator + "bin" + separator + "java";
      ProcessBuilder processBuilder = new ProcessBuilder(java, "-cp",  classpath, DuplicateProviderRegistrationTest.class.getCanonicalName());
      processBuilder.redirectErrorStream(true);
      
      try
      {
         System.out.println("Starting JVM");
         process = processBuilder.start();
         System.out.println("Started JVM");
      } catch (IOException e1)
      {
         e1.printStackTrace();
      }
   }
   
   public static class MyFeature implements Feature
   {
      @Override
      public boolean configure(FeatureContext featureContext)
      {
         // MyFilter instance will be registered third on the same
         // featureContext even if
         // featureContext.getConfiguration().isRegistered(MyFilter.class)==true
         featureContext.register(new MyFilter());
         return true;
      }
   }

   public static class MyFilter implements ClientRequestFilter
   {
      @Override
      public void filter(ClientRequestContext clientRequestContext) throws IOException
      {
         System.out.println(MyFilter.class.getName());
      }
   }

   public static class MyInterceptor implements ReaderInterceptor
   {
      @Override
      public Object aroundReadFrom(ReaderInterceptorContext context) throws IOException, WebApplicationException
      {
         return null;
      }
   }
   
   @Test
   public void runTests() throws Exception
   {
      deployJVM();
      InputStream is = process.getInputStream();
      StringBuilder sb = new StringBuilder();
      int c = is.read();
      while (c != -1)
      {
         sb.append((char) c);
         c = is.read();
      }
      Assert.assertEquals(7, countWarnings(sb.toString()));
      process.destroy();
   }
   
   public void testDuplicateProvider()
   {
      Client client = ClientBuilder.newClient();
      try
      {
         WebTarget webTarget = client.target("http://www.changeit.com");
         // MyFeature will be registered third on the same webTarget even if
         // webTarget.getConfiguration().isRegistered(MyFeature.class)==true
         webTarget.register(MyFeature.class).register(new MyFeature()).register(new MyFeature());
      }
      finally
      {
         client.close();
      }
   }

   /**
    * Tests taken from javax.ws.rs.core.Configurable javadoc.
    * @throws Exception
    */
   public void testFromJavadoc()
   {
      Client client = ClientBuilder.newClient();
      try
      {
         WebTarget webTarget = client.target("http://www.changeit.com");
         webTarget.register(MyInterceptor.class, ReaderInterceptor.class);
         webTarget.register(MyInterceptor.class);       // Rejected by runtime.
         webTarget.register(new MyInterceptor());       // Rejected by runtime.
         webTarget.register(MyInterceptor.class, 6500); // Rejected by runtime.

         webTarget.register(new MyFeature());
         webTarget.register(MyFeature.class);   // rejected by runtime.
         webTarget.register(MyFeature.class, Feature.class);  // Rejected by runtime.
      }
      finally
      {
         client.close();
      }
   }
   
   int countWarnings(String s)
   {
      int count = 0;
      int i = s.indexOf("RESTEASY002155");
      while (i >= 0)
      {
         count++;
         s = s.substring(i + "RESTEASY002155".length());
         i = s.indexOf("RESTEASY002155");
      }
      return count;
   }
}