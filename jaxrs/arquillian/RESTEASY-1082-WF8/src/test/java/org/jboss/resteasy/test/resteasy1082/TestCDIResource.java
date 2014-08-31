package org.jboss.resteasy.test.resteasy1082;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * RESTEASY-1082.
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Created August 21, 2014
 */
@RunWith(Arquillian.class)
public class TestCDIResource
{
   @Test
   public void testCDIResourceFromServlet() throws Exception
   {
      Path from = FileSystems.getDefault().getPath("target/RESTEASY-1082.war").toAbsolutePath();
      Path to = FileSystems.getDefault().getPath("target/wildfly-8.1.0.Final/standalone/deployments/RESTEASY-1082.war").toAbsolutePath();

      try
      {
         // Delete existing RESTEASY-1082.war, if any.
         try
         {
            Files.delete(to);
         }
         catch (Exception e)
         {
            // ok
         }

         // Deploy RESTEASY-1082.war
         Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING);
         System.out.println("Copied war to " + to);
         DefaultHttpClient client = new DefaultHttpClient();
         HttpGet get = new HttpGet("http://localhost:8080/RESTEASY-1082/test");

         // Wait for RESTEASY-1082.war to be installed.
         HttpResponse response = client.execute(get);
         while (response.getStatusLine().getStatusCode() == 404)
         {
            Thread.sleep(1000);
            get.releaseConnection();
            response = client.execute(get);
         }
         System.out.println("status: " + response.getStatusLine().getStatusCode());
         printResponse(response);
         Assert.assertEquals(200, response.getStatusLine().getStatusCode());
         get.releaseConnection();
         
         // Redeploy RESTEASY-1082.war
         Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING);
         System.out.println("Replaced war");
         Thread.sleep(5000);
         
         // Wait for RESTEASY-1082.war to be installed.
         response = client.execute(get);
         while (response.getStatusLine().getStatusCode() == 404)
         {
            Thread.sleep(1000);
            get.releaseConnection();
            response = client.execute(get);
         }
         System.out.println("status: " + response.getStatusLine().getStatusCode());
         printResponse(response);
         Assert.assertEquals(200, response.getStatusLine().getStatusCode());
      }
      finally
      {
         Files.delete(to);
      }
   }

   protected void printResponse(HttpResponse response) throws IOException
   {
      System.out.println("response: ");
      BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
      String line = reader.readLine();
      while (line != null)
      {
         System.out.println(line);
         line = reader.readLine();
      }
   }
}
