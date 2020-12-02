package org.jboss.resteasy.test.providers.datasource.resource;

import javax.activation.DataSource;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import java.io.InputStream;

@Path("/")
public class CleanFilesDataSourceProviderResource {

   public static String clientResponse = "response_from_client";

   @GET
   @Path("/tmpdirpath")
   @Produces("text/plain")
   public String get() {
      return System.getProperty("java.io.tmpdir");
   }

   @POST
   @Path("once")
   @Produces("text/plain")
   public String get(DataSource dataSource) throws Exception {
      InputStream is = dataSource.getInputStream();
      // read the input stream as a test
      while (is.read() != -1) {
      }
      return clientResponse;
   }

   @POST
   @Path("twice")
   @Produces("text/plain")
   public String getInputSteamTwice(DataSource dataSource) throws Exception {
      InputStream is = dataSource.getInputStream();
      // read the input stream as a test
      while (is.read() != -1) {
      }
      InputStream is2 = dataSource.getInputStream();
      is2.close();
      return clientResponse;
   }

   @POST
   @Path("never")
   @Produces("text/plain")
   public String postDataSource(DataSource datasource) {
      return datasource.getContentType();
   }

}
