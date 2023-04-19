package org.jboss.resteasy.test.providers.resource;

import java.lang.annotation.Annotation;
import java.sql.Date;

import org.jboss.resteasy.spi.StringParameterUnmarshaller;

public class ProviderFactoryStrParamUnmarshaller implements StringParameterUnmarshaller<Date> {

    @Override
    public void setAnnotations(Annotation[] annotations) {
    }

    @Override
    public Date fromString(String str) {
        return null;
    }

}
