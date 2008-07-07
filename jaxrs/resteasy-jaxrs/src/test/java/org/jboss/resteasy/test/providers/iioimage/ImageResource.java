/**
 *
 */
package org.jboss.resteasy.test.providers.iioimage;

import javax.imageio.IIOImage;
import javax.ws.rs.ConsumeMime;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.ProduceMime;

import org.jboss.resteasy.annotations.providers.ImageWriterParams;

/**
 * 
 * A ImageResource.
 * 
 * @author <a href="ryan@damnhandy.com>Ryan J. McDonough</a>
 * @version $Revision: $
 */
@Path("/image")
public class ImageResource
{

   /**
    * FIXME Comment this
    * 
    * @param image
    * @return
    */
   @POST
   @ConsumeMime("image/*")
   @ProduceMime("image/png")
   @ImageWriterParams(compressionQuality = 0.2f)
   public IIOImage transcodeImage(IIOImage image)
   {
      return image;
   }


}
