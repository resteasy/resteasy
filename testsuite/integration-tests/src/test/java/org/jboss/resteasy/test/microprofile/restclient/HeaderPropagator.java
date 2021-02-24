package org.jboss.resteasy.test.microprofile.restclient;

import java.util.List;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;

import org.eclipse.microprofile.rest.client.ext.ClientHeadersFactory;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

public class HeaderPropagator implements ClientHeadersFactory {
   @Override
   public MultivaluedMap<String, String> update(MultivaluedMap<String, String> containerRequestHeaders, MultivaluedMap<String, String> clientRequestHeaders) {
       if (ResteasyProviderFactory.getContextData(HttpHeaders.class) != null) {
           throw new RuntimeException("ResteasyProviderFactory.getContextData(HttpHeaders.class) is not null");
       }
       if (ResteasyProviderFactory.getContextData(HttpHeaders.class) != null) {
           throw new RuntimeException("ResteasyProviderFactory.getContextData(HttpHeaders.class) is not null");
       }
       List<String> prop = containerRequestHeaders.get("X-Propagated");
       if(prop != null)
          clientRequestHeaders.addAll("X-Propagated", prop);
       return clientRequestHeaders;
   }
}