/**
 *
 */
package org.jboss.resteasy.test.providers.datasource;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.FileRequestEntity;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
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
      HttpClient client = new HttpClient();
      //File file = new File("./src/test/test-data/harper.jpg");
      File file = LocateTestData.getTestData("harper.jpg");
      Assert.assertTrue(file.exists());
      PostMethod method = new PostMethod(TEST_URI);
      method.setRequestEntity(new FileRequestEntity(file, "image/jpeg"));
      int status = client.executeMethod(method);
      Assert.assertEquals(HttpServletResponse.SC_OK, status);
      Assert.assertEquals("image/jpeg", method.getResponseBodyAsString());
      method.releaseConnection();
   }

   @Test
   public void testEchoDataSourceBigData() throws Exception
   {
      HttpClient client = new HttpClient();
      File file = LocateTestData.getTestData("harper.jpg");
      Assert.assertTrue(file.exists());
      PostMethod method = new PostMethod(TEST_URI + "/echo");
      method.setRequestEntity(new FileRequestEntity(file, "image/jpeg"));
      int status = client.executeMethod(method);
      Assert.assertEquals(HttpServletResponse.SC_OK, status);

      InputStream ris = null;
      InputStream fis = null;
      try
      {
         ris = method.getResponseBodyAsStream();
         fis = new FileInputStream(file);
         int fi;
         int ri;
         do
         {
            fi = fis.read();
            ri = ris.read();
            if (fi != ri)
               Assert.fail("The sent and recived stream is not identical.");
         } while (fi != -1);
      }
      finally
      {
         if (ris != null)
            ris.close();
         if (fis != null)
            fis.close();
      }

      method.releaseConnection();
   }

   @Test
   public void testEchoDataSourceSmallData() throws Exception
   {
      HttpClient client = new HttpClient();
      byte[] input = "Hello World!".getBytes("utf-8");
      PostMethod method = new PostMethod(TEST_URI + "/echo");
      method.setRequestEntity(new ByteArrayRequestEntity(input, MediaType.APPLICATION_OCTET_STREAM));
      int status = client.executeMethod(method);
      Assert.assertEquals(HttpServletResponse.SC_OK, status);

      InputStream ris = null;
      InputStream bis = null;
      try
      {
         ris = method.getResponseBodyAsStream();
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

      method.releaseConnection();
   }

   @Test
   public void testGetDataSource() throws Exception
   {
      HttpClient client = new HttpClient();
      String value = "foo";
      GetMethod method = new GetMethod(TEST_URI + "/" + value);
      int status = client.executeMethod(method);
      Assert.assertEquals(HttpServletResponse.SC_OK, status);
      Assert.assertEquals(value, method.getResponseBodyAsString());
      method.releaseConnection();
   }
}
