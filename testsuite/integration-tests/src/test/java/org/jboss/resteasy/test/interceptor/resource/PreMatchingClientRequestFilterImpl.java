package org.jboss.resteasy.test.interceptor.resource;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.core.Response;

/**
 * Annotation PreMatching is not valid for ClientRequestFilterImpl.
 * This test confirms RESTEasy ignores the annotation and logs a warning msg.
 *
 * Created by rsearls on 8/21/17.
 */
@PreMatching
public class PreMatchingClientRequestFilterImpl implements ClientRequestFilter {

   @Override
   public void filter(ClientRequestContext requestContext) {
      requestContext.abortWith(Response.status(404).build());
   }
}
