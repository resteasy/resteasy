package org.jboss.resteasy.plugins.interceptors;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import jakarta.ws.rs.ConstrainedTo;
import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.container.DynamicFeature;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.FeatureContext;

import org.jboss.resteasy.annotations.ContentEncoding;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@ConstrainedTo(RuntimeType.SERVER)
public class ServerContentEncodingAnnotationFeature implements DynamicFeature {
    @Override
    public void configure(ResourceInfo resourceInfo, FeatureContext configurable) {
        @SuppressWarnings("rawtypes")
        final Class declaring = resourceInfo.getResourceClass();
        final Method method = resourceInfo.getResourceMethod();

        if (declaring == null || method == null)
            return;
        Set<String> encodings = getEncodings(method.getAnnotations());
        if (encodings.size() <= 0) {
            encodings = getEncodings(declaring.getAnnotations());
            if (encodings.size() <= 0)
                return;
        }
        // check if GZIP encoder has been registered
        if (!isGZipRegistered(configurable.getConfiguration())) {
            encodings.remove("gzip");
        }
        configurable.register(createFilter(encodings));
    }

    protected boolean isGZipRegistered(Configuration configuration) {
        return configuration.isRegistered(GZIPEncodingInterceptor.class);
    }

    protected ServerContentEncodingAnnotationFilter createFilter(Set<String> encodings) {
        return new ServerContentEncodingAnnotationFilter(encodings);
    }

    protected Set<String> getEncodings(Annotation[] annotations) {
        Set<String> encodings = new HashSet<String>();
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().isAnnotationPresent(ContentEncoding.class)) {
                encodings.add(annotation.annotationType().getAnnotation(ContentEncoding.class).value().toLowerCase());
            }
        }
        return encodings;
    }
}
