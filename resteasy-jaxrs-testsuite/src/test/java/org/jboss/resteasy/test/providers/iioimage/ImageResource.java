/**
 *
 */
package org.jboss.resteasy.test.providers.iioimage;

import org.jboss.resteasy.annotations.providers.img.ImageWriterParams;

import javax.imageio.IIOImage;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
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
   @Consumes("image/*")
   @Produces("image/png")
   @ImageWriterParams(compressionQuality = 0.2f)
   public IIOImage transcodeImage(IIOImage image)
   {
      return image;
   }


}
