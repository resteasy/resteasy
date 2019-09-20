package org.jboss.resteasy.microprofile.client;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.junit.Test;

import java.net.URI;

public class RESTEASY_2335_Test {
   public static final String HTTP_LOCALHOST_8080 = "http://localhost:8080";

   @Test
   public void test2335() {
      RestClientBuilder.newBuilder().baseUri(URI.create(HTTP_LOCALHOST_8080)).build(RESTEASY_2335_Resource.class);
   }
}
