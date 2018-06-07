package org.jboss.resteasy.client.jaxrs.internal;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;

public interface ClientInvocationBuilderInterface extends Invocation.Builder
{
   ClientRequestHeaders getHeaders();
   void setChunked(boolean chunked);
   public ClientInvocation getInvocation();
   public void setInvocation(ClientInvocation invocation);
   public void setTarget(WebTarget target);
}
