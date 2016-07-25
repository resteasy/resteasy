package org.jboss.resteasy.test.providers.resource;

import org.jboss.resteasy.spi.StringParameterUnmarshaller;

import java.lang.annotation.Annotation;
import java.sql.Date;

public class ProviderFactoryStrParamUnmarshaller implements StringParameterUnmarshaller<Date> {

    @Override
    public void setAnnotations(Annotation[] annotations) {
    }

    @Override
    public Date fromString(String str) {
        return null;
    }

}
