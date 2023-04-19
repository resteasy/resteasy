package org.jboss.resteasy.plugins.providers;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.annotation.Priority;
import javax.ws.rs.Consumes;
import javax.ws.rs.Encoded;
import javax.ws.rs.Priorities;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.util.FindAnnotation;

@Priority(Priorities.AUTHORIZATION)
@Provider
@Produces("application/x-www-form-urlencoded")
@Consumes("application/x-www-form-urlencoded")
public class JaxrsServerFormUrlEncodedProvider implements MessageBodyReader<Form> {
    protected boolean useContainerParams;

    public JaxrsServerFormUrlEncodedProvider(final boolean useContainerParams) {
        this.useContainerParams = useContainerParams;
    }

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return (useContainerParams && Form.class.isAssignableFrom(type));
    }

    @Context
    HttpRequest request;

    @SuppressWarnings("unchecked")
    @Override
    public Form readFrom(Class<Form> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
        LogMessages.LOGGER.debugf("Provider : %s,  Method : readFrom", getClass().getName());
        MultivaluedMap<String, String> map = null;
        if (useContainerParams) {
            boolean encoded = FindAnnotation.findAnnotation(annotations, Encoded.class) != null;
            if (encoded) {
                map = request.getFormParameters();
            } else {
                map = request.getDecodedFormParameters();
            }
        } else {
            map = new FormUrlEncodedProvider().readFrom(null, null,
                    annotations, mediaType, httpHeaders, entityStream);
        }
        return new Form(map);
    }
}
