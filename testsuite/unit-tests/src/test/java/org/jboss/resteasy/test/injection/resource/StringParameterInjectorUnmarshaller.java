package org.jboss.resteasy.test.injection.resource;


import org.jboss.resteasy.spi.StringParameterUnmarshaller;
import javax.ws.rs.core.Context;
import java.lang.annotation.Annotation;

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
