package org.jboss.resteasy.client.jaxrs.engines;

import org.apache.http.protocol.HttpContext;

public interface HttpContextProvider
{
   HttpContext getContext();
}
