/**
 *
 */
package org.jboss.resteasy.test.providers.multipart;

import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.ws.rs.ConsumeMime;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.ProduceMime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author <a href="mailto:ryan@damnhandy.com">Ryan J. McDonough</a>
 * 
 */
@Path("/mime")
public class SimpleMimeMultipartResource
{

   private static final Logger logger = LoggerFactory.getLogger(SimpleMimeMultipartResource.class);

   /**
    * @param multipart
    * @return
    */
   @PUT
   @ConsumeMime("multipart/form-data")
   @ProduceMime("text/plain")
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

   //    @POST
   //    @ConsumeMime("multipart/form-data")
   //    @ProduceMime("text/plain")
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
   @ProduceMime("multipart/mixed")
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
