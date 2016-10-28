package org.jboss.resteasy.test.providers.datasource.resource;


import javax.activation.DataSource;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Path("/jaf")
public class BigSmallDataSourceResource {

    @POST
    @Consumes("image/jpeg")
    @Produces("text/plain")
    public String postDataSource(DataSource datasource) {
        return datasource.getContentType();
    }

    @POST
    @Path("/echo")
    public DataSource echo(DataSource datasource) {
        return datasource;
    }

    @GET
    @Path("/{value}")
    public DataSource getDataSource(@PathParam("value") String value) throws IOException {
        final byte[] bytes = value.getBytes();
        DataSource ds = new DataSource() {
            public InputStream getInputStream() throws IOException {
                return new ByteArrayInputStream(bytes);
            }

            public OutputStream getOutputStream() throws IOException {
                throw new IOException("not allowed");
            }

            public String getContentType() {
                return "text/plain";
            }

            public String getName() {
                return "";
            }
        };
        return ds;
    }
}
