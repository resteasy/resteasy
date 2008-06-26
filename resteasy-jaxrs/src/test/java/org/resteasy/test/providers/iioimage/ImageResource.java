/**
 *
 */
package org.resteasy.test.providers.iioimage;

import javax.imageio.IIOImage;
import javax.ws.rs.ConsumeMime;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.ProduceMime;

/**
 * @author <a href="mailto:ryan@damnhandy.com">Ryan J. McDonough</a>
 *         Jun 23, 2008
 */
@Path("/image")
public class ImageResource
{

   @POST
   @ConsumeMime("image/*")
   @ProduceMime("image/png")
   public IIOImage transcodeImage(IIOImage image)
   {
      return image;
   }


}
