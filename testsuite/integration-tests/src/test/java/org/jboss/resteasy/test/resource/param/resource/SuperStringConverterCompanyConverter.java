package org.jboss.resteasy.test.resource.param.resource;

import javax.ws.rs.ext.ParamConverter;

import javax.ws.rs.ext.Provider;

@Provider
public class SuperStringConverterCompanyConverter extends SuperStringConverterObjectConverter<SuperStringConverterCompany> implements ParamConverter<SuperStringConverterCompany> {
    public SuperStringConverterCompany fromString(String value) {
        return new SuperStringConverterCompany(value);
    }

}
