package org.jboss.resteasy.resteasy1407;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.io.InputStream;

/**
 * RESTEASY-1407
 *
 * @author Dmitrii Tikhomirov
 */

@Path("/message")
public class ByteArrayInputStreamZeroLengthServiceImpl implements ByteArrayInputStreamZeroLengthService {

    @Override
    public Response upload(InputStream data) {
        return Response.ok().build();
    }

}
