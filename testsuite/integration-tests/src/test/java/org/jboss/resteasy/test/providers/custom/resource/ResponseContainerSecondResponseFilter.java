package org.jboss.resteasy.test.providers.custom.resource;

import java.lang.annotation.Annotation;

import jakarta.annotation.Priority;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.Provider;

// reverse order, This filter should be second
@Provider
@Priority(100)
public class ResponseContainerSecondResponseFilter extends ResponseContainerTemplateFilter {
    @Override
    protected void operationMethodNotFound(String operation) {
        // the check is to apply on ResponseFilter only
        // here, it is usually not found.
    }

    public void setEntity() {
        MediaType type = responseContext.getMediaType();
        if (assertTrue(MediaType.APPLICATION_SVG_XML_TYPE.equals(type),
                "Unexpected mediatype", type)) {
            return;
        }

        Annotation[] annotations = responseContext.getEntityAnnotations();
        for (Annotation annotation : annotations) {
            Class<?> clazz = annotation.annotationType();
            if (assertTrue(clazz == Provider.class
                    || clazz == Priority.class, "Annotation", clazz,
                    "was unexpected")) {
                return;
            }
        }
    }
}
