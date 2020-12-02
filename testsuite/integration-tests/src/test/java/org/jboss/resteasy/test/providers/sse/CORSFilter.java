package org.jboss.resteasy.test.providers.sse;

import java.io.IOException;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CORSFilter implements ContainerResponseFilter
{
   @Override
   public void filter(ContainerRequestContext request, ContainerResponseContext response) throws IOException
   {
      MultivaluedMap<String, Object> headers = response.getHeaders();
      headers.add("Access-Control-Allow-Origin", "*");
      headers.add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
      headers.add("Access-Control-Allow-Credentials", "true");
      headers.add("Access-Control-Max-Age", "1209600");
      headers.add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization, ETag, if-none-match");
      headers.add("Access-Control-Expose-Headers", "origin, content-type, accept, authorization, ETag, if-none-match");
      headers.add("x-foo-by", "rs");
   }
}
