package org.jboss.resteasy.test.core.interceptors.resource;

import java.io.IOException;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.ext.Provider;
import org.jboss.resteasy.util.CookieUtil;

@Provider
@PreMatching
public class FilteredCookieContainerRequestFilter implements ContainerRequestFilter {

   private static final String OLD_COOKIE_NAME = "old-cookie";
   private static final String NEW_COOKIE_NAME = "new-cookie";

   @Override
   public void filter(ContainerRequestContext requestContext) throws IOException {

      final Cookie cookie = requestContext.getCookies().get(OLD_COOKIE_NAME);
      if (cookie != null) {
         Cookie ck1 = new Cookie.Builder(NEW_COOKIE_NAME)
                 .value(cookie.getValue())
                 .build();
         requestContext.getHeaders().add(HttpHeaders.COOKIE,
                 CookieUtil.toString(Cookie.class, ck1));
      }
   }
}
