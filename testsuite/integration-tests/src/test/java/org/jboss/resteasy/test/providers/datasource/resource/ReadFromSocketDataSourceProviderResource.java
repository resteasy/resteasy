package org.jboss.resteasy.test.providers.datasource.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

@Path("/")
public class ReadFromSocketDataSourceProviderResource {

   public static final int KBs = 5;
   public static final int SIZE = KBs * 1024;

   @GET
   @Produces("text/plain")
   public String get() {
      StringBuffer buffer = new StringBuffer();
      for (int i = 0; i < SIZE; i++) {
         buffer.append("x");
      }
      return buffer.toString();
   }

}
