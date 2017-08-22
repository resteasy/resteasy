package org.jboss.resteasy.test.interceptor.resource;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Response;

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
