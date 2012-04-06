/**
 *
 */
package org.jboss.resteasy.test.providers.datasource;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.test.LocateTestData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import static org.jboss.resteasy.test.TestPortProvider.*;

/**
 * @author <a href="mailto:ryan@damnhandy.com">Ryan J. McDonough</a> Jun 23,
 *         2008
 */
public class TestDataSourceProvider extends BaseResourceTest
{

   private static final String TEST_URI = generateURL("/jaf");

   /**
    * @throws java.lang.Exception
    */
   @Before
   public void setUp() throws Exception
   {
      addPerRequestResource(DataSourceResource.class);
   }

   @Test
   public void testPostDataSource() throws Exception
   {
      //File file = new File("./src/test/test-data/harper.jpg");
      File file = LocateTestData.getTestData("harper.jpg");
      Assert.assertTrue(file.exists());
      ClientRequest request = new ClientRequest(TEST_URI);
      request.body("image/jpeg", file);
      ClientResponse<?> response = request.post();
      Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
      Assert.assertEquals("image/jpeg", response.getEntity(String.class));      
   }

   @Test
   public void testEchoDataSourceBigData() throws Exception
   {
      ClientRequest request = new ClientRequest(TEST_URI + "/echo");
      File file = LocateTestData.getTestData("harper.jpg");
      Assert.assertTrue(file.exists());
      request.body("image/jpeg", file);
      ClientResponse<?> response = request.post();
      Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
      
      InputStream ris = null;
      InputStream fis = null;
      try
      {
         ris = response.getEntity(InputStream.class);
         fis = new FileInputStream(file);
         int fi;
         int ri;
         do
         {
            fi = fis.read();
            ri = ris.read();
            if (fi != ri)
               Assert.fail("The sent and received stream is not identical.");
         } while (fi != -1);         
      }
      finally
      {
         if (ris != null)
            ris.close();
         if (fis != null)
            fis.close();
      }
   }

   @Test
   public void testEchoDataSourceSmallData() throws Exception
   {
      ClientRequest request = new ClientRequest(TEST_URI + "/echo");
      byte[] input = "Hello World!".getBytes("utf-8");
      request.body(MediaType.APPLICATION_OCTET_STREAM, input);
      ClientResponse<?> response = request.post();
      Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
      
      InputStream ris = null;
      InputStream bis = null;
      try
      {
         ris = response.getEntity(InputStream.class);
         bis = new ByteArrayInputStream(input);
         int fi;
         int ri;
         do
         {
            fi = bis.read();
            ri = ris.read();
            if (fi != ri)
               Assert.fail("The sent and recived stream is not identical.");
         } while (fi != -1);
      }
      finally
      {
         if (ris != null)
            ris.close();
         if (bis != null)
            bis.close();
      }
   }

   @Test
   public void testGetDataSource() throws Exception
   {
      String value = "foo";
      ClientRequest request = new ClientRequest(TEST_URI + "/" + value);
      ClientResponse<?> response = request.get();
      Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());
      Assert.assertEquals(value, response.getEntity(String.class));
   }
}
