/**
 *
 */
package org.jboss.resteasy.test.providers.multipart;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.annotations.providers.multipart.PartType;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartInput;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import java.io.IOException;
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

   public static class Form2
   {
      @FormParam("submit-name")
      public String name;

      @FormParam("files")
      public byte[] file;
   }

   private static final Logger logger = LoggerFactory.getLogger(SimpleMimeMultipartResource.class);

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
               logger.debug(multipart.getBodyPart(i).getContent().toString());
               logger.debug("bytes available {}", multipart.getBodyPart(i).getInputStream().available());
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
   public void putMultipartFormData(MultipartFormDataInput multipart) throws IOException
   {
      Assert.assertEquals(2, multipart.getParts().size());

      Assert.assertTrue(multipart.getFormData().containsKey("bill"));
      Assert.assertTrue(multipart.getFormData().containsKey("monica"));

      System.out.println(multipart.getFormData().get("bill").getBodyAsString());
      Customer cust = multipart.getFormDataPart("bill", Customer.class, null);
      Assert.assertNotNull(cust);
      Assert.assertEquals("bill", cust.getName());

      cust = multipart.getFormDataPart("monica", Customer.class, null);
      Assert.assertNotNull(cust);
      Assert.assertEquals("monica", cust.getName());

   }

   /**
    * @param multipart
    * @return
    */
   @PUT
   @Path("form/map")
   @Consumes("multipart/form-data")
   public void putMultipartMap(Map<String, Customer> multipart) throws IOException
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

      Customer cust = multipart.getParts().get(0).getBody(Customer.class, null);
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

      Customer cust = multipart.getParts().get(0).getBody(Customer.class, null);
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

   //    @POST
   //    @Consumes("multipart/form-data")
   //    @Produces("text/plain")
   //    public String putData(MultiPartEntity entity) {
   //        StringBuilder b = new StringBuilder("Elements: ");
   //        b.append(entity.getPart(0, String.class));
   //        b.append(entity.getPart(1, String.class));
   //        return b.toString();
   //    }

   /**
    * @return
    */
   @GET
   @Produces("multipart/mixed")
   public MimeMultipart getMimeMultipart() throws MessagingException
   {
      MimeMultipart multipart = new MimeMultipart("mixed");
      multipart.addBodyPart(createPart("Body of part 1", "text/plain", "This is a description"));
      multipart.addBodyPart(createPart("Body of part 2", "text/plain", "This is another description"));
      return multipart;
   }

   private MimeBodyPart createPart(String value, String type, String description) throws MessagingException
   {
      MimeBodyPart part = new MimeBodyPart();
      part.setDescription(description);
      part.setContent(value, type);
      return part;
   }
}
