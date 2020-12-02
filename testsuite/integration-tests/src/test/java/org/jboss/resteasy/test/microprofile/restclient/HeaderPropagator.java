package org.jboss.resteasy.test.microprofile.restclient;

import java.util.List;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MultivaluedMap;

import org.eclipse.microprofile.rest.client.ext.ClientHeadersFactory;
import org.jboss.resteasy.core.ResteasyContext;
import org.junit.Assert;

public class HeaderPropagator implements ClientHeadersFactory {
   @Override
   public MultivaluedMap<String, String> update(MultivaluedMap<String, String> containerRequestHeaders, MultivaluedMap<String, String> clientRequestHeaders) {
       Assert.assertNull(ResteasyContext.getContextData(HttpHeaders.class));
       List<String> prop = containerRequestHeaders.get("X-Propagated");
       if(prop != null)
          clientRequestHeaders.addAll("X-Propagated", prop);
       return clientRequestHeaders;
   }
}