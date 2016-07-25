package org.jboss.resteasy.test.form.resource;

import org.junit.Assert;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Path("/")
public class FormUrlEncodedResource {
    @Path("/simple")
    @POST
    public StreamingOutput post(@QueryParam("hello") String abs, InputStream entityStream) throws IOException {
        Assert.assertNull(abs);
        final InputStream is = entityStream;
        return new StreamingOutput() {
            public void write(OutputStream output) throws IOException {
                int c;
                while ((c = is.read()) != -1) {
                    output.write(c);
                }
            }
        };
    }

}
