package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.ext.ParamConverter;

public class MultiValuedParamDefaultParamConverterParamConverter implements ParamConverter<MultiValuedParamDefaultParamConverterParamConverterClass> {

   @Override
   public MultiValuedParamDefaultParamConverterParamConverterClass fromString(String value) {
       MultiValuedParamDefaultParamConverterParamConverterClass pc = new MultiValuedParamDefaultParamConverterParamConverterClass();
       pc.setS("p" + value);
       return pc;
   }

   @Override
   public String toString(MultiValuedParamDefaultParamConverterParamConverterClass value) {
      return "p" + value.getS();
   }
}
