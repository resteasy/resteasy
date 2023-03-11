package org.jboss.resteasy.core.interception.jaxrs;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.ReaderInterceptor;

import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.tracing.RESTEasyTracingLogger;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ClientReaderInterceptorContext extends AbstractReaderInterceptorContext {
    protected Map<String, Object> properties;

    @Deprecated
    public ClientReaderInterceptorContext(final ReaderInterceptor[] interceptors, final ResteasyProviderFactory providerFactory,
            final Class type,
            final Type genericType, final Annotation[] annotations, final MediaType mediaType,
            final MultivaluedMap<String, String> headers, final InputStream inputStream,
            final Map<String, Object> properties) {
        super(mediaType, providerFactory, annotations, interceptors, headers, genericType, type, inputStream);
        this.properties = properties;
    }

    public ClientReaderInterceptorContext(final ReaderInterceptor[] interceptors, final ResteasyProviderFactory providerFactory,
            final Class type,
            final Type genericType, final Annotation[] annotations, final MediaType mediaType,
            final MultivaluedMap<String, String> headers, final InputStream inputStream,
            final Map<String, Object> properties, final RESTEasyTracingLogger tracingLogger) {
        super(mediaType, providerFactory, annotations, interceptors, headers, genericType, type, inputStream, tracingLogger);
        this.properties = properties;
    }

    protected void throwReaderNotFound() {
        throw new ProcessingException(Messages.MESSAGES.clientResponseFailureMediaType(mediaType, type));
    }

    @SuppressWarnings(value = "unchecked")
    @Override
    protected MessageBodyReader resolveReader(MediaType mediaType) {
        return providerFactory.getClientMessageBodyReader(type,
                genericType, annotations, mediaType);
    }

    @Override
    public Object getProperty(String name) {
        return properties.get(name);
    }

    @Override
    public Collection<String> getPropertyNames() {
        return properties.keySet();
    }

    @Override
    public void setProperty(String name, Object object) {
        if (object == null) {
            properties.remove(name);
        } else {
            properties.put(name, object);
        }
    }

    @Override
    public void removeProperty(String name) {
        properties.remove(name);
    }
}
