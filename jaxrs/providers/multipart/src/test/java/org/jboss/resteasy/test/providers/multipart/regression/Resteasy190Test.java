package org.jboss.resteasy.test.providers.multipart.regression;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.annotations.providers.multipart.PartType;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartOutput;
import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.test.LocateTestData;
import static org.jboss.resteasy.test.TestPortProvider.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.mail.BodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * See RESTEASY-190
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class Resteasy190Test extends BaseResourceTest
{

   private static final String TEST_URI = generateURL("/mime");


   public static class MyBean
   {
      @FormParam("someBinary")
      @PartType(MediaType.APPLICATION_OCTET_STREAM)
      private InputStream someBinary;


      public InputStream getSomeBinary()
      {
         return someBinary;
      }

      public void setSomeBinary(InputStream someBinary)
      {
         this.someBinary = someBinary;
      }
   }

   @Path("/mime")
   public static class MyService
   {

      @POST
      public Response createMyBean(@Context HttpHeaders headers, String str)
      {
         System.out.println("Content-Type: " + headers.getMediaType());
         System.out.println(str);

         return Response.ok(str, headers.getMediaType()).build();
      }

      @GET
      @Produces(MediaType.MULTIPART_FORM_DATA)
      @MultipartForm
      public MyBean createMyBean()
      {
         MyBean myBean = new MyBean();
         myBean.setSomeBinary(new ByteArrayInputStream("bla".getBytes()));

         return myBean;
      }
   }

   /**
    * @throws java.lang.Exception
    */
   @Before
   public void setUp() throws Exception
   {
      dispatcher.getRegistry().addPerRequestResource(MyService.class);
   }

   /**
    * This is here for future debugging purposes as it is just a loopback to examine form-data
    *
    * @throws Exception
    */
   @Test
   public void testPost() throws Exception
   {
      ClientRequest request = new ClientRequest(TEST_URI);
      MultipartOutput mpo = new MultipartOutput();
      mpo.addPart("This is Value 1", MediaType.TEXT_PLAIN_TYPE);
      mpo.addPart("This is Value 2", MediaType.TEXT_PLAIN_TYPE);
      mpo.addPart(LocateTestData.getTestData("data.txt"), MediaType.TEXT_PLAIN_TYPE);
      request.body(MediaType.MULTIPART_FORM_DATA_TYPE, mpo);
      ClientResponse<InputStream> response = request.post(InputStream.class);
      BufferedInputStream in = new BufferedInputStream(response.getEntity());
      String contentType = response.getResponseHeaders().getFirst("content-type");
      System.out.println(contentType);
      ByteArrayDataSource ds = new ByteArrayDataSource(in, contentType);
      MimeMultipart mimeMultipart = new MimeMultipart(ds);
      Assert.assertEquals(mimeMultipart.getCount(), 3);
      response.releaseConnection();
   }

   @Test
   public void testPostForm() throws Exception
   {
      ClientRequest request = new ClientRequest(TEST_URI);
      MultipartFormDataOutput mpfdo = new MultipartFormDataOutput();
      mpfdo.addFormData("part1", "This is Value 1", MediaType.TEXT_PLAIN_TYPE);
      mpfdo.addFormData("part2", "This is Value 2", MediaType.TEXT_PLAIN_TYPE);
      mpfdo.addFormData("data.txt", LocateTestData.getTestData("data.txt"), MediaType.TEXT_PLAIN_TYPE);
      request.body(MediaType.MULTIPART_FORM_DATA_TYPE, mpfdo);
      ClientResponse<InputStream> response = request.post(InputStream.class);
      BufferedInputStream in = new BufferedInputStream(response.getEntity());
      String contentType = response.getResponseHeaders().getFirst("content-type");
      System.out.println(contentType);
      ByteArrayDataSource ds = new ByteArrayDataSource(in, contentType);
      MimeMultipart mimeMultipart = new MimeMultipart(ds);
      Assert.assertEquals(mimeMultipart.getCount(), 3);
      response.releaseConnection();
   }
   
   @Test
   public void testGet() throws Exception
   {
      ClientRequest request = new ClientRequest(TEST_URI);
      ClientResponse<InputStream> response = request.get(InputStream.class);
      Assert.assertEquals(HttpServletResponse.SC_OK, response.getStatus());  
      BufferedInputStream in = new BufferedInputStream(response.getEntity());
      String contentType = response.getResponseHeaders().getFirst("content-type");
      ByteArrayDataSource ds = new ByteArrayDataSource(in, contentType);
      MimeMultipart mimeMultipart = new MimeMultipart(ds);
      Assert.assertEquals(mimeMultipart.getCount(), 1);

      BodyPart part = mimeMultipart.getBodyPart(0);
      InputStream is = part.getInputStream();

      Assert.assertEquals(3, part.getSize());


      char[] output = new char[3];
      output[0] = (char) is.read();
      output[1] = (char) is.read();
      output[2] = (char) is.read();
      String str = new String(output);
      Assert.assertEquals("bla", str);

   }


}
