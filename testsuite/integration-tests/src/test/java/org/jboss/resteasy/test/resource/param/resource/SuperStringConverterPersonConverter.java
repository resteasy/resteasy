package org.jboss.resteasy.test.resource.param.resource;

import javax.ws.rs.ext.ParamConverter;

import javax.ws.rs.ext.Provider;

@Provider
public class SuperStringConverterPersonConverter extends SuperStringConverterSuperPersonConverter implements ParamConverter<SuperStringConverterPerson> {
}
