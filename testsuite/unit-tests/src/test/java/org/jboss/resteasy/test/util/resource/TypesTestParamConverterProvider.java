package org.jboss.resteasy.test.util.resource;

import javax.ws.rs.ext.ParamConverter;

public class TypesTestParamConverterProvider implements ParamConverter<TypesParamConverterPOJO> {

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
