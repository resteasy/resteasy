package org.jboss.resteasy.test.providers.datasource.resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.activation.DataSource;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.apache.http.entity.StringEntity;

@Path("/upload")
public class ReadDataSourceResource {
    public static final String PATH_UPLOAD = "upload";

    @POST
    @Produces("text/plain")
    public Response read(final DataSource ds) throws Exception {

        final String content1 = readStream(ds);
        final String content2 = readStream(ds);
        if (content1.equals(content2)) {
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new StringEntity("Content from second read does not match content from first read")).build();
        }

    }

    private BufferedReader getReader(final DataSource ds) throws IOException {
        return new BufferedReader(new InputStreamReader(ds.getInputStream()));
    }

    private String readStream(final DataSource ds) throws IOException {
        final StringBuilder stringBuilder = new StringBuilder(4100);
        final BufferedReader bufferedReader = getReader(ds);
        try {
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            return stringBuilder.toString();
        } finally {
            bufferedReader.close();
        }
    }
}
