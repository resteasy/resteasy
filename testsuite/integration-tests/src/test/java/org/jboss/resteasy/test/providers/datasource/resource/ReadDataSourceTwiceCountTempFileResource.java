package org.jboss.resteasy.test.providers.datasource.resource;

import org.jboss.logging.Logger;

import javax.activation.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.InputStream;

@Path("/")
public class ReadDataSourceTwiceCountTempFileResource {

    protected static final Logger logger = Logger.getLogger(ReadDataSourceTwiceCountTempFileResource.class.getName());

    @Path("post")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    //@Produces(MediaType.TEXT_PLAIN)
    @POST
    public Response post(DataSource source) throws Exception {
        InputStream is = source.getInputStream();
        while (is.read() > -1) {
        }
        logger.info("Readed once, going to read second");
        InputStream is2 = source.getInputStream();
        is2.close();
        // return Response.ok().entity(countTempFiles()).type(MediaType.WILDCARD_TYPE).build();
        return Response.ok().entity(countTempFiles()).build();
    }

    private int countTempFiles() throws Exception {
        String tmpdir = System.getProperty("java.io.tmpdir");
        logger.info("tmpdir: " + tmpdir);
        File dir = new File(tmpdir);
        int counter = 0;
        for (File file : dir.listFiles()) {
            if (file.getName().startsWith("resteasy-provider-datasource")) {
                counter++;
            }
        }
        return counter;
    }
}
