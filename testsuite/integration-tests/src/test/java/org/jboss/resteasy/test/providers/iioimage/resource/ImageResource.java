package org.jboss.resteasy.test.providers.iioimage.resource;

import javax.imageio.IIOImage;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import org.jboss.resteasy.annotations.providers.img.ImageWriterParams;

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
