package org.jboss.resteasy.resteasy1407;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;

/**
 * RESTEASY-1407
 *
 * @author Dmitrii Tikhomirov
 */

public interface ByteArrayInputStreamZeroLengthService {

    @POST
    @Path("/upload")
    @Consumes("*/*")
    @Produces(MediaType.TEXT_PLAIN)
    Response upload(InputStream data);

}

