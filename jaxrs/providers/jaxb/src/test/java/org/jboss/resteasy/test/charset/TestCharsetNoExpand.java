package org.jboss.resteasy.test.charset;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * Unit tests for RESTEASY-1066.
 * 
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @date Aug 13, 2014
 */
public class TestCharsetNoExpand extends TestCharsetParent
{
   protected static Process process;

   @BeforeClass
   public static void deployServer() throws InterruptedException
   {
      System.out.println("server default charset: " + Charset.defaultCharset());
      Map<String, Charset> charsets = Charset.availableCharsets();
      Charset charset = null;
      for (Iterator<String> it = charsets.keySet().iterator(); it.hasNext(); )
      {
         String cs = it.next();
         if (!cs.equals(Charset.defaultCharset().name()))
         {
            charset = charsets.get(cs);
            break;
         }
      }
      System.out.println("server using charset: " + charset);

      System.out.println(TestServerExpand.class.getCanonicalName());
      String separator = System.getProperty("file.separator");
      String classpath = System.getProperty("java.class.path");
      String path = System.getProperty("java.home") + separator + "bin" + separator + "java";
      System.out.println("classpath: " + classpath);
      System.out.println("path: " + path);
      ProcessBuilder processBuilder = new ProcessBuilder(path, "-cp",  classpath, TestServerNoExpand.class.getCanonicalName());
      
      try
      {
         System.out.println("Starting server JVM");
         process = processBuilder.start();
         System.out.println("Started server JVM");
      } catch (IOException e1)
      {
         e1.printStackTrace();
      }
    
      ClientRequest request = new ClientRequest(generateURL("/junk"));
      ClientResponse<?> response = null;
      while (true)
      {
         try
         {
            response = request.get();
            if (response.getStatus() == 200)
            {
            	System.out.println("Server started: " + response.getEntity(String.class));
               break;
            }
            System.out.println("Waiting on server ...");
         }
         catch (Exception e)
         {
            // keep trying
         }
         System.out.println("Waiting for server");
         Thread.sleep(1000);
      }

   }

   @AfterClass
   public static void after() throws Exception
   {
      process.destroy();
      System.out.println("Process destroyed.");
   }
}