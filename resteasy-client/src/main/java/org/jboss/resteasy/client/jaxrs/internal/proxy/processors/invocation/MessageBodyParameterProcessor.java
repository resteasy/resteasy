package org.jboss.resteasy.client.jaxrs.internal.proxy.processors.invocation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.MediaType;

import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;
import org.jboss.resteasy.client.jaxrs.internal.proxy.processors.InvocationProcessor;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MessageBodyParameterProcessor implements InvocationProcessor {
    private Class<?> type;
    private MediaType mediaType;
    private Type genericType;
    private Annotation[] annotations;

    public MessageBodyParameterProcessor(final MediaType mediaType, final Class<?> type, final Type genericType,
            final Annotation[] annotations) {
        this.type = type;
        this.mediaType = mediaType;
        this.genericType = genericType;
        this.annotations = annotations;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void process(ClientInvocation invocation, Object param) {
        // Check if a previous HTTP header was set for the media type. This could have been done with a
        // @HeaderParam("Content-Type") annotation on a method parameter in the proxy. If so, we'll use that over the
        // assumed media type.
        MediaType mediaType = invocation.getHeaders().getMediaType();
        if (mediaType == null) {
            mediaType = this.mediaType;
        }
        invocation.setEntity(
                Entity.entity(param == null ? null : new GenericEntity<Object>(param, genericType), mediaType, annotations));
    }

    public Class<?> getType() {
        return type;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

}
