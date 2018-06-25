package org.jboss.resteasy.test.exception.resource;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

@Provider
@Priority(Priorities.USER)
public class ResponseExceptionMapperRuntimeExceptionMapper implements ResponseExceptionMapper<RuntimeException>
{

   @Override
   public RuntimeException toThrowable(Response response)
   {
      switch (response.getStatus())
      {
         case 406:
            return new RuntimeException(response.readEntity(String.class));
         case 500:
            return new RuntimeException(response.readEntity(String.class));   
      }
      return null;
   }

   @Override
   public boolean handles(int status, MultivaluedMap<String, Object> headers)
   {
      return status == 406 || status == 500;
   }

}
