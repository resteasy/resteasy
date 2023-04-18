package org.jboss.resteasy.test.resource.param.resource;

import javax.ws.rs.ext.ParamConverter;

public abstract class SuperStringConverterSuperPersonConverter implements ParamConverter<SuperStringConverterPerson> {
    public SuperStringConverterPerson fromString(String value) {
        return new SuperStringConverterPerson(value);
    }

    public String toString(SuperStringConverterPerson value) {
        return value.getName();
    }
}
