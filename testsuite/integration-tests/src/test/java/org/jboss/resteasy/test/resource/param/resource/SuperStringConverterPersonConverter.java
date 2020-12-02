package org.jboss.resteasy.test.resource.param.resource;

import jakarta.ws.rs.ext.ParamConverter;

import jakarta.ws.rs.ext.Provider;

@Provider
public class SuperStringConverterPersonConverter extends SuperStringConverterSuperPersonConverter implements ParamConverter<SuperStringConverterPerson> {
}
