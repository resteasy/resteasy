package org.jboss.resteasy.test.util.resource;

import org.jboss.resteasy.spi.MarshalledEntity;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.ParamConverter;

public class TypesTestProvider implements ExceptionMapper<NullPointerException>, MarshalledEntity<Integer>, ParamConverter<TypesParamConverterPOJO> {

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

   @Override
   public TypesParamConverterPOJO fromString(String str) {
      TypesParamConverterPOJO pojo = new TypesParamConverterPOJO();
      pojo.setName(str);
      return pojo;
   }

   @Override
   public String toString(TypesParamConverterPOJO value) {
      return value.getName();
   }
}
