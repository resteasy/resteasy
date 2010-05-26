package org.jboss.resteasy.client.core.extractors;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.core.BaseClientResponse;

public interface ResponseHandler
{
   @SuppressWarnings("unchecked")
   Object getResponseObject(ClientRequest request, BaseClientResponse response, Object... args);
}
