package org.jboss.resteasy.test.providers.iioimage.resource;

import org.jboss.resteasy.annotations.providers.img.ImageWriterParams;

import javax.imageio.IIOImage;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/image")
public class ImageResource {
    @POST
    @Consumes("image/*")
    @Produces("image/png")
    @ImageWriterParams(compressionQuality = 0.2f)
    public IIOImage transcodeImage(IIOImage image) {
        return image;
    }
}
