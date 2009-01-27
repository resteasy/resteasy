package org.jboss.resteasy.test.providers.multipart.regression;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.annotations.providers.multipart.PartType;
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
import java.util.ArrayList;
import java.util.List;

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
      HttpClient client = new HttpClient();
      List<Part> partsList = new ArrayList<Part>();
      partsList.add(new StringPart("part1", "This is Value 1"));
      partsList.add(new StringPart("part2", "This is Value 2"));
      partsList.add(new FilePart("data.txt", LocateTestData.getTestData("data.txt")));
      Part[] parts = partsList.toArray(new Part[partsList.size()]);
      PostMethod method = new PostMethod(TEST_URI);
      RequestEntity entity = new MultipartRequestEntity(parts, method.getParams());
      method.setRequestEntity(entity);
      int status = client.executeMethod(method);
      Assert.assertEquals(200, status);

      InputStream response = method.getResponseBodyAsStream();
      BufferedInputStream in = new BufferedInputStream(response);
      String contentType = method.getResponseHeader("content-type").getValue();
      System.out.println(contentType);
      ByteArrayDataSource ds = new ByteArrayDataSource(in, contentType);
      MimeMultipart mimeMultipart = new MimeMultipart(ds);
      Assert.assertEquals(mimeMultipart.getCount(), 3);
      method.releaseConnection();

   }

   @Test
   public void testGet() throws Exception
   {
      HttpClient client = new HttpClient();
      GetMethod method = new GetMethod(TEST_URI);
      int status = client.executeMethod(method);
      Assert.assertEquals(HttpServletResponse.SC_OK, status);
      InputStream response = method.getResponseBodyAsStream();
      BufferedInputStream in = new BufferedInputStream(response);
      String contentType = method.getResponseHeader("content-type").getValue();
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
