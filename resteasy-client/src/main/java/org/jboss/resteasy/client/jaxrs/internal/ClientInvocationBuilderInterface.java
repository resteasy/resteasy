package org.jboss.resteasy.client.jaxrs.internal;

import javax.ws.rs.client.Invocation;

public interface ClientInvocationBuilderInterface extends Invocation.Builder
{
   ClientRequestHeaders getHeaders();
   void setChunked(boolean chunked);
   public ClientInvocation getInvocation();
}
