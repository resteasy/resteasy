package org.jboss.resteasy.test.resource.param.resource;

import org.jboss.resteasy.spi.StringConverter;

import javax.ws.rs.ext.Provider;

@Provider
public class SuperStringConverterPersonConverter extends SuperStringConverterSuperPersonConverter implements StringConverter<SuperStringConverterPerson> {
}
