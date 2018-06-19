package org.jboss.resteasy.client.microprofile;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.ws.rs.client.ResponseProcessingException;

import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.internal.ClientConfiguration;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;
import org.jboss.resteasy.client.jaxrs.internal.ClientRequestContextImpl;
import org.jboss.resteasy.client.jaxrs.internal.ClientRequestHeaders;
import org.jboss.resteasy.client.jaxrs.internal.ClientResponse;

/**
 * An extension of ClientInvocation for implementing MP REST Client
 * 
 * @author <a href="mailto:alessio.soldano@jboss.com">Alessio Soldano</a>
 *
 */
public class MPClientInvocation extends ClientInvocation
{

   public MPClientInvocation(ResteasyClient client, URI uri, ClientRequestHeaders headers, ClientConfiguration parent)
   {
      super(client, uri, headers, parent);
   }
   
   protected MPClientInvocation(ClientInvocation clientInvocation)
   {
      super(clientInvocation);
   }
   
   @SuppressWarnings({"rawtypes", "unchecked"})
   protected ClientResponse filterResponse(ClientRequestContextImpl requestContext, ClientResponse _response)
   {
      ClientResponse response = super.filterResponse(requestContext, _response);
      Map<ResponseExceptionMapper, Integer> mappers = new HashMap<>();
      Set<Object> instances = configuration.getInstances();
      for (Object instance : instances) {
         if(instance instanceof ResponseExceptionMapper) {
            ResponseExceptionMapper candidate = (ResponseExceptionMapper) instance;
            if (candidate.handles(response.getStatus(), response.getHeaders())) {
               mappers.put(candidate, candidate.getPriority());
            }
         }
      }

      if(mappers.size()>0) {
         Map<Optional<Throwable>, Integer> errors = new HashMap<>();

         mappers.forEach( (m, i) -> {
            Optional<Throwable> t = Optional.ofNullable(m.toThrowable(response));
            errors.put(t, i);
         });

         Optional<Throwable> prioritised = Optional.empty();
         for (Map.Entry<Optional<Throwable>,Integer> errorEntry : errors.entrySet()) {
            if(errorEntry.getKey().isPresent()) {
               if(!prioritised.isPresent() || errorEntry.getValue() < errors.get(prioritised)) {
                  prioritised = errorEntry.getKey();
               }
            }
         }

         // strange rule from the spec
         if(prioritised.isPresent()) {
            Throwable t = prioritised.get();
            if (t instanceof RuntimeException) {
               throw (RuntimeException) t;
            } else {
               // for checked exceptions
               throw new ResponseProcessingException(response, t);
            }
         }
      }

      return response;
   }
}
