package org.jboss.resteasy.test.client.resource;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class RequestFilterAcceptLanguage implements ClientRequestFilter {
   @Override
   public void filter(ClientRequestContext requestContext) throws IOException {
      List<Locale> locales = requestContext.getAcceptableLanguages();
      StringBuilder builder = new StringBuilder();
      for (Locale locale : locales) {
         builder.append(locale.toString()).append(",");
      }
      Response r = Response.ok(builder.toString()).build();
      requestContext.abortWith(r);
   }
}
