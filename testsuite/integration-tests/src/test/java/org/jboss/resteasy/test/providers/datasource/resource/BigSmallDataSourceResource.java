package org.jboss.resteasy.test.providers.datasource.resource;


import javax.activation.DataSource;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
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
