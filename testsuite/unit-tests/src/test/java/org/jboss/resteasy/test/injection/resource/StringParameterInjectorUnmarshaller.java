package org.jboss.resteasy.test.injection.resource;

import java.lang.annotation.Annotation;

import jakarta.ws.rs.core.Context;

import org.jboss.resteasy.spi.StringParameterUnmarshaller;

public class StringParameterInjectorUnmarshaller implements
        StringParameterUnmarshaller<String> {

    @Context
    private StringParameterInjectorInjected in;

    @Override
    public void setAnnotations(Annotation[] annotations) {
    }

    @Override
    public String fromString(String str) {
        return in.value;
    }

}
