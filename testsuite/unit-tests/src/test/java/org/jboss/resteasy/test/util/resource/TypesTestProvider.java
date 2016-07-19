package org.jboss.resteasy.test.util.resource;

import org.jboss.resteasy.spi.MarshalledEntity;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class TypesTestProvider implements ExceptionMapper<NullPointerException>, MarshalledEntity<Integer> {

    public Response toResponse(NullPointerException exception) {
        return null;
    }

   @Override
   public byte[] getMarshalledBytes()
   {
      return null;
   }

   @Override
   public Integer getEntity()
   {
      return null;
   }

}
