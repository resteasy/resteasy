/**
 *
 */
package org.jboss.resteasy.test.providers.multipart;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.annotations.providers.multipart.PartType;
import org.jboss.resteasy.annotations.providers.multipart.XopWithMultipartRelated;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartConstants;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartInput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartRelatedInput;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:ryan@damnhandy.com">Ryan J. McDonough</a>
 */
@Path("/mime")
public class SimpleMimeMultipartResource
{

   public static class Form
   {
      @FormParam("bill")
      @PartType("application/xml")
      private Customer bill;

      @FormParam("monica")
      @PartType("application/xml")
      private Customer monica;

      public Form()
      {
      }

      public Form(Customer bill, Customer monica)
      {
         this.bill = bill;
         this.monica = monica;
      }

      public Customer getBill()
      {
         return bill;
      }

      public Customer getMonica()
      {
         return monica;
      }
   }

   @XmlRootElement
   @XmlAccessorType(XmlAccessType.FIELD)
   public static class Xop
   {
      private Customer bill;

      private Customer monica;

      @XmlMimeType(MediaType.APPLICATION_OCTET_STREAM)
      private byte[] myBinary;

      @XmlMimeType(MediaType.APPLICATION_OCTET_STREAM)
      private DataHandler myDataHandler;

      public Xop()
      {
      }

      public Xop(Customer bill, Customer monica, byte[] myBinary,
                 DataHandler myDataHandler)
      {
         this.bill = bill;
         this.monica = monica;
         this.myBinary = myBinary;
         this.myDataHandler = myDataHandler;
      }

      public Customer getBill()
      {
         return bill;
      }

      public Customer getMonica()
      {
         return monica;
      }

      public byte[] getMyBinary()
      {
         return myBinary;
      }

      public DataHandler getMyDataHandler()
      {
         return myDataHandler;
      }
   }

   public static class Form2
   {
      @FormParam("submit-name")
      public String name;

      @FormParam("files")
      public byte[] file;
   }

   private static final Logger logger = LoggerFactory
           .getLogger(SimpleMimeMultipartResource.class);

   @POST
   @Path("file/test")
   @Consumes(MediaType.MULTIPART_FORM_DATA)
   @Produces("text/html")
   public String post(@MultipartForm Form2 form)
   {
      Assert.assertEquals("Bill", form.name.trim());
      Assert.assertEquals("hello world", new String(form.file).trim());
      return "hello world";
   }

   /**
    * @param multipart
    * @return
    */
   @PUT
   @Consumes("multipart/form-data")
   @Produces("text/plain")
   public String putData(MimeMultipart multipart)
   {
      StringBuilder b = new StringBuilder("Count: ");
      try
      {
         b.append(multipart.getCount());
         for (int i = 0; i < multipart.getCount(); i++)
         {
            try
            {
               logger.debug(multipart.getBodyPart(i).getContent()
                       .toString());
               logger.debug("bytes available {}", multipart.getBodyPart(i)
                       .getInputStream().available());
            }
            catch (IOException e)
            {
               // TODO Auto-generated catch block
               e.printStackTrace();
            }
         }
      }
      catch (MessagingException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      return b.toString();
   }

   /**
    * @param multipart
    * @return
    */
   @PUT
   @Path("form")
   @Consumes("multipart/form-data")
   public void putMultipartFormData(MultipartFormDataInput multipart)
           throws IOException
   {
      Assert.assertEquals(2, multipart.getParts().size());

      Assert.assertTrue(multipart.getFormDataMap().containsKey("bill"));
      Assert.assertTrue(multipart.getFormDataMap().containsKey("monica"));

      System.out.println(multipart.getFormDataMap().get("bill").get(0)
              .getBodyAsString());
      Customer cust = multipart.getFormDataPart("bill", Customer.class, null);
      Assert.assertNotNull(cust);
      Assert.assertEquals("bill", cust.getName());

      cust = multipart.getFormDataPart("monica", Customer.class, null);
      Assert.assertNotNull(cust);
      Assert.assertEquals("monica", cust.getName());

   }

   @PUT
   @Path("related")
   @Consumes(MultipartConstants.MULTIPART_RELATED)
   public void putMultipartRelated(MultipartRelatedInput multipart)
           throws IOException
   {
      Assert.assertEquals(MultipartConstants.APPLICATION_XOP_XML, multipart.getType());
      Assert
              .assertEquals("<mymessage.xml@example.org>", multipart
                      .getStart());
      Assert.assertEquals("text/xml", multipart.getStartInfo());
      Assert.assertEquals(3, multipart.getParts().size());
      Iterator<InputPart> inputParts = multipart.getParts().iterator();
      Assert.assertEquals(inputParts.next(), multipart.getRootPart());
      InputPart rootPart = multipart.getRootPart();

      Assert.assertEquals("application", rootPart.getMediaType().getType());
      Assert.assertEquals("xop+xml", rootPart.getMediaType().getSubtype());
      Assert.assertEquals("UTF-8", rootPart.getMediaType().getParameters()
              .get("charset"));
      Assert.assertEquals("text/xml", rootPart.getMediaType().getParameters()
              .get("type"));
      Assert.assertEquals("<mymessage.xml@example.org>", rootPart
              .getHeaders().getFirst("Content-ID"));
      Assert.assertEquals("8bit", rootPart.getHeaders().getFirst(
              "Content-Transfer-Encoding"));
      Assert
              .assertEquals(
                      "<m:data xmlns:m='http://example.org/stuff'>"
                              + "<m:photo><xop:Include xmlns:xop='http://www.w3.org/2004/08/xop/include' href='cid:http://example.org/me.png'/></m:photo>"
                              + "<m:sig><xop:Include xmlns:xop='http://www.w3.org/2004/08/xop/include' href='cid:http://example.org/my.hsh'/></m:sig>"
                              + "</m:data>", rootPart.getBodyAsString());

      InputPart relatedPart1 = inputParts.next();
      Assert.assertEquals("image", relatedPart1.getMediaType().getType());
      Assert.assertEquals("png", relatedPart1.getMediaType().getSubtype());
      Assert.assertEquals("<http://example.org/me.png>", relatedPart1
              .getHeaders().getFirst("Content-ID"));
      Assert.assertEquals("binary", relatedPart1.getHeaders().getFirst(
              "Content-Transfer-Encoding"));
      Assert.assertEquals("// binary octets for png", relatedPart1
              .getBodyAsString());

      InputPart relatedPart2 = inputParts.next();
      Assert.assertEquals("application", relatedPart2.getMediaType()
              .getType());
      Assert.assertEquals("pkcs7-signature", relatedPart2.getMediaType()
              .getSubtype());
      Assert.assertEquals("<http://example.org/me.hsh>", relatedPart2
              .getHeaders().getFirst("Content-ID"));
      Assert.assertEquals("binary", relatedPart2.getHeaders().getFirst(
              "Content-Transfer-Encoding"));
      Assert.assertEquals("// binary octets for signature", relatedPart2
              .getBodyAsString());
   }

   /**
    * @param multipart
    * @return
    */
   @PUT
   @Path("form/map")
   @Consumes("multipart/form-data")
   public void putMultipartMap(Map<String, Customer> multipart)
           throws IOException
   {
      Assert.assertEquals(2, multipart.size());

      Assert.assertTrue(multipart.containsKey("bill"));
      Assert.assertTrue(multipart.containsKey("monica"));

      Customer cust = multipart.get("bill");
      Assert.assertNotNull(cust);
      Assert.assertEquals("bill", cust.getName());

      cust = multipart.get("monica");
      Assert.assertNotNull(cust);
      Assert.assertEquals("monica", cust.getName());

   }

   /**
    * @param multipart
    * @return
    */
   @PUT
   @Path("multi")
   @Consumes("multipart/form-data")
   public void putMultipartData(MultipartInput multipart) throws IOException
   {
      Assert.assertEquals(2, multipart.getParts().size());

      Customer cust = multipart.getParts().get(0).getBody(Customer.class,
              null);
      Assert.assertNotNull(cust);
      Assert.assertEquals("bill", cust.getName());

      cust = multipart.getParts().get(1).getBody(Customer.class, null);
      Assert.assertNotNull(cust);
      Assert.assertEquals("monica", cust.getName());

   }

   /**
    * @param multipart
    * @return
    */
   @PUT
   @Path("mixed")
   @Consumes("multipart/mixed")
   public void putMultipartMixed(MultipartInput multipart) throws IOException
   {
      Assert.assertEquals(2, multipart.getParts().size());

      Customer cust = multipart.getParts().get(0).getBody(Customer.class,
              null);
      Assert.assertNotNull(cust);
      Assert.assertEquals("bill", cust.getName());

      cust = multipart.getParts().get(1).getBody(Customer.class, null);
      Assert.assertNotNull(cust);
      Assert.assertEquals("monica", cust.getName());

   }

   /**
    * @param multipart
    * @return
    */
   @PUT
   @Path("multi/list")
   @Consumes("multipart/form-data")
   public void putMultipartList(List<Customer> multipart) throws IOException
   {
      Assert.assertEquals(2, multipart.size());

      Customer cust = multipart.get(0);
      Assert.assertNotNull(cust);
      Assert.assertEquals("bill", cust.getName());

      cust = multipart.get(1);
      Assert.assertNotNull(cust);
      Assert.assertEquals("monica", cust.getName());

   }

   /**
    * @param multipart
    * @return
    */
   @PUT
   @Path("form/class")
   @Consumes("multipart/form-data")
   public void putMultipartForm(@MultipartForm Form form) throws IOException
   {
      Assert.assertNotNull(form.getBill());
      Assert.assertEquals("bill", form.getBill().getName());

      Assert.assertNotNull(form.getMonica());
      Assert.assertEquals("monica", form.getMonica().getName());
   }

   @PUT
   @Path("xop")
   @Consumes(MultipartConstants.MULTIPART_RELATED)
   public void putXopWithMultipartRelated(@XopWithMultipartRelated Xop xop)
           throws IOException
   {
      Assert.assertNotNull(xop.getBill());
      Assert.assertEquals("bill\u00E9", xop.getBill().getName());

      Assert.assertNotNull(xop.getMonica());
      Assert.assertEquals("monica", xop.getMonica().getName());
      Assert.assertNotNull(xop.getMyBinary());
      Assert.assertNotNull(xop.getMyDataHandler());
      Assert.assertEquals("Hello Xop World!", new String(xop.getMyBinary(),
              "UTF-8"));
      // lets do it twice to test that we get different InputStream-s each
      // time.
      for (int fi = 0; fi < 2; fi++)
      {
         InputStream inputStream = xop.getMyDataHandler().getInputStream();
         InputStreamReader inputStreamReader = null;
         try
         {
            inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            StringWriter writer = new StringWriter();
            char[] buffer = new char[4048];
            int n = 0;
            while ((n = inputStreamReader.read(buffer)) != -1)
               writer.write(buffer, 0, n);
            Assert.assertEquals("Hello Xop World!", writer.toString());
         }
         finally
         {
            if (inputStreamReader != null)
               inputStreamReader.close();
            inputStream.close();
         }
      }
   }

   /**
    * @param multipart
    * @return
    */
   @PUT
   @Path("text")
   @Consumes("multipart/form-data")
   @Produces("text/plain")
   public void putData(String multipart)
   {
      System.out.println(multipart);
   }

   // @POST
   // @Consumes("multipart/form-data")
   // @Produces("text/plain")
   // public String putData(MultiPartEntity entity) {
   // StringBuilder b = new StringBuilder("Elements: ");
   // b.append(entity.getPart(0, String.class));
   // b.append(entity.getPart(1, String.class));
   // return b.toString();
   // }

   /**
    * @return
    */
   @GET
   @Produces("multipart/mixed")
   public MimeMultipart getMimeMultipart() throws MessagingException
   {
      MimeMultipart multipart = new MimeMultipart("mixed");
      multipart.addBodyPart(createPart("Body of part 1", "text/plain",
              "This is a description"));
      multipart.addBodyPart(createPart("Body of part 2", "text/plain",
              "This is another description"));
      return multipart;
   }

   private MimeBodyPart createPart(String value, String type,
                                   String description) throws MessagingException
   {
      MimeBodyPart part = new MimeBodyPart();
      part.setDescription(description);
      part.setContent(value, type);
      return part;
   }
}
