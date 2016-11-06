package org.jboss.resteasy.test.resource.param.resource;

import org.jboss.resteasy.spi.StringConverter;

import javax.ws.rs.ext.Provider;

@Provider
public class SuperStringConverterCompanyConverter extends SuperStringConverterObjectConverter<SuperStringConverterCompany> implements StringConverter<SuperStringConverterCompany> {
    public SuperStringConverterCompany fromString(String value) {
        return new SuperStringConverterCompany(value);
    }

}
