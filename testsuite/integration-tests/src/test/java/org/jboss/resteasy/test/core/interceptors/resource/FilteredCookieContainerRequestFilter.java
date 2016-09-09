package org.jboss.resteasy.test.core.interceptors.resource;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;

@Provider
@PreMatching
public class FilteredCookieContainerRequestFilter implements ContainerRequestFilter {
   
   private static final String OLD_COOKIE_NAME = "old-cookie";
   private static final String NEW_COOKIE_NAME = "new-cookie";
   
   @Override
   public void filter(ContainerRequestContext requestContext) throws IOException {
      
      final Cookie cookie = requestContext.getCookies().get(OLD_COOKIE_NAME);
      if (cookie != null) {
         requestContext.getHeaders().add(HttpHeaders.COOKIE, new Cookie(NEW_COOKIE_NAME, cookie.getValue()).toString());
      }
   }
}
