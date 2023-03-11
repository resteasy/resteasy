package org.jboss.resteasy.test.resource.param.resource;

import java.lang.annotation.Annotation;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jboss.resteasy.spi.StringParameterUnmarshaller;
import org.jboss.resteasy.spi.util.FindAnnotation;
import org.jboss.resteasy.test.resource.param.StringParamUnmarshallerTest;

public class StringParamUnmarshallerDateFormatter implements StringParameterUnmarshaller<Date> {
    private SimpleDateFormat formatter;

    public void setAnnotations(Annotation[] annotations) {
        StringParamUnmarshallerTest.StringParamUnmarshallerDateFormat format = FindAnnotation.findAnnotation(annotations,
                StringParamUnmarshallerTest.StringParamUnmarshallerDateFormat.class);
        formatter = new SimpleDateFormat(format.value());
    }

    public Date fromString(String str) {
        try {
            return formatter.parse(str);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
