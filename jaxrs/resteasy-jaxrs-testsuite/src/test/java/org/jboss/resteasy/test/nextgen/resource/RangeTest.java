package org.jboss.resteasy.test.nextgen.resource;

import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class RangeTest extends BaseResourceTest
{
   @Path("/")
   public static class Resource {
      @GET
      @Path("file")
      @Produces("text/plain")
      public File getFile()
      {
         return file;
      }
   }




   static Client client;
   static File file;

   @BeforeClass
   public static void setup()
   {
      addPerRequestResource(Resource.class);
      client = ClientBuilder.newClient();
      try
      {
         file = File.createTempFile("tmp", "tmp");
         FileOutputStream fos = new FileOutputStream(file);
         for (int i = 0; i < 1000; i++)
         {
            fos.write("hello".getBytes());
         }
         fos.write("1234".getBytes());
         fos.flush();
         fos.close();
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }

   @AfterClass
   public static void cleanup()
   {
      client.close();
      file.delete();
   }

   @Test
   public void testDate()
   {
      SimpleDateFormat dateFormatRFC822 = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
      dateFormatRFC822.setTimeZone(TimeZone.getTimeZone("GMT"));
      String format = dateFormatRFC822.format(new Date());
      System.out.println(format);
      try
      {
         Date date = dateFormatRFC822.parse(format);
         System.out.println(date.toString());
      }
      catch (ParseException e)
      {
         throw new RuntimeException(e);
      }
   }


   @Test
   public void testRange0to3()
   {
      Response response = client.target(generateURL("/file")).request()
              .header("Range", "bytes=0-3").get();
      Assert.assertEquals(response.getStatus(), 206);
      Assert.assertEquals(4, response.getLength());
      System.out.println("Content-Range: " + response.getHeaderString("Content-Range"));
      Assert.assertEquals(response.readEntity(String.class), "hell");
      response.close();
   }

   @Test
   public void testRange1to4()
   {
      Response response = client.target(generateURL("/file")).request()
              .header("Range", "bytes=1-4").get();
      Assert.assertEquals(response.getStatus(), 206);
      Assert.assertEquals(4, response.getLength());
      System.out.println("Content-Range: " + response.getHeaderString("Content-Range"));
      Assert.assertEquals(response.readEntity(String.class), "ello");
      response.close();
   }

   @Test
   public void testRange0to3000()
   {
      Response response = client.target(generateURL("/file")).request()
              .header("Range", "bytes=0-3000").get();
      Assert.assertEquals(response.getStatus(), 206);
      Assert.assertEquals(3001, response.getLength());
      System.out.println("Content-Range: " + response.getHeaderString("Content-Range"));
      byte[] bytes = response.readEntity( new GenericType<byte[]>(){});
      Assert.assertEquals(3001, bytes.length);
      response.close();
   }

   @Test
   public void testNegative4()
   {
      Response response = client.target(generateURL("/file")).request()
              .header("Range", "bytes=-4").get();
      Assert.assertEquals(response.getStatus(), 206);
      Assert.assertEquals(4, response.getLength());
      System.out.println("Content-Range: " + response.getHeaderString("Content-Range"));
      Assert.assertEquals(response.readEntity(String.class), "1234");
      response.close();
   }

   @Test
   public void testNegative6000()
   {
      Response response = client.target(generateURL("/file")).request()
              .header("Range", "bytes=-6000").get();
      Assert.assertEquals(response.getStatus(), 200);
      response.close();
   }



}
