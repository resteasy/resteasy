package org.jboss.resteasy.client.microprofile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.client.ResponseProcessingException;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

class ExceptionMapping implements ClientResponseFilter
{
   private Set<Object> instances;

   ExceptionMapping(Set<Object> instances)
   {
      this.instances = instances;
   }

   @Override
   public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException
   {

      Response response = new PartialResponse(responseContext);

      Map<ResponseExceptionMapper<?>, Integer> mappers = new HashMap<>();
      for (Object o : instances)
      {
         if (o instanceof ResponseExceptionMapper)
         {
            ResponseExceptionMapper<?> candiate = (ResponseExceptionMapper<?>) o;
            if (candiate.handles(response.getStatus(), response.getHeaders()))
            {
               mappers.put(candiate, candiate.getPriority());
            }
         }
      }

      if (mappers.size() > 0)
      {
         Map<Optional<Throwable>, Integer> errors = new HashMap<>();

         mappers.forEach((m, i) -> {
            Optional<Throwable> t = Optional.ofNullable(m.toThrowable(response));
            errors.put(t, i);
         });

         Optional<Throwable> prioritised = Optional.empty();
         for (Optional<Throwable> throwable : errors.keySet())
         {
            if (throwable.isPresent())
            {
               if (!prioritised.isPresent())
               {
                  prioritised = throwable;
               }
               else if (errors.get(throwable) < errors.get(prioritised))
               {
                  prioritised = throwable;
               }
            }
         }

         response.bufferEntity();
         if (prioritised.isPresent())
         { // strange rule from the spec
            Throwable t = prioritised.get();
            if (t instanceof RuntimeException)
            {
               throw (RuntimeException) t;
            }
            else
            {
               // for checked exceptions
               throw new ResponseProcessingException(response, t);
            }
         }
      }

   }
}
