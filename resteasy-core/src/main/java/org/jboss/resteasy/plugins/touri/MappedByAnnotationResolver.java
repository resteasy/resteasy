package org.jboss.resteasy.plugins.touri;

import java.lang.annotation.Annotation;

import org.jboss.resteasy.specimpl.ResteasyUriBuilderImpl;
import org.jboss.resteasy.spi.ResteasyUriBuilder;
import org.jboss.resteasy.spi.touri.MappedBy;

public class MappedByAnnotationResolver extends
        AbstractURITemplateAnnotationResolver {
    protected Class<? extends Annotation> getAnnotationType() {
        return MappedBy.class;
    }

    protected ResteasyUriBuilder getUriBuilder(Class<? extends Object> clazz) {
        MappedBy mappedBy = clazz.getAnnotation(MappedBy.class);
        ResteasyUriBuilder uriBuilderImpl = new ResteasyUriBuilderImpl();
        Class<?> resourceType = mappedBy.resource();
        uriBuilderImpl.path(resourceType);
        String method = mappedBy.method();
        if (method != null && method.length() > 0) {
            uriBuilderImpl.path(resourceType, method);
        }
        return uriBuilderImpl;
    }
}
