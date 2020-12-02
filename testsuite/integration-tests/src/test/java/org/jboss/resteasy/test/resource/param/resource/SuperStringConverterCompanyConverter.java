package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.ext.ParamConverter;

import jakarta.ws.rs.ext.Provider;

@Provider
public class SuperStringConverterCompanyConverter extends SuperStringConverterObjectConverter<SuperStringConverterCompany> implements ParamConverter<SuperStringConverterCompany> {
   public SuperStringConverterCompany fromString(String value) {
      return new SuperStringConverterCompany(value);
   }

}
