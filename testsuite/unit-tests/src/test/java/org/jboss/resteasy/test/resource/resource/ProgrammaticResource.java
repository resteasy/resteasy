package org.jboss.resteasy.test.resource.resource;

import jakarta.ws.rs.core.Configurable;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.UriInfo;

import org.junit.Assert;

public class ProgrammaticResource {

   private UriInfo uriInfo;

   public int counter;

   private HttpHeaders headers;

   private Configurable<?> configurable;

   public void setHeaders(HttpHeaders headers)
   {
      this.headers = headers;
   }

   public ProgrammaticResource() {
   }

   public ProgrammaticResource(final Configurable<?> configurable) {
      this.configurable = configurable;
   }

   public String get(String param) {
      Assert.assertEquals("hello", param);
      uriInfo.getBaseUri();
      headers.getCookies();
      configurable.getConfiguration();
      counter++;
      return "hello";
   }

   public void put(String value) {
      Assert.assertEquals("hello", value);
   }
}
