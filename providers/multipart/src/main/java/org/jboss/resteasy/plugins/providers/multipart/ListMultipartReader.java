package org.jboss.resteasy.plugins.providers.multipart;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.Providers;

import org.jboss.resteasy.plugins.providers.multipart.i18n.Messages;
import org.jboss.resteasy.spi.util.Types;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
@Consumes("multipart/*")
public class ListMultipartReader implements MessageBodyReader<List<?>> {
    protected @Context Providers workers;

    public boolean isReadable(Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType) {
        return type.equals(List.class) && genericType != null
                && genericType instanceof ParameterizedType;
    }

    public List<?> readFrom(Class<List<?>> type, Type genericType,
            Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
            throws IOException, WebApplicationException {
        String boundary = mediaType.getParameters().get("boundary");
        if (boundary == null)
            throw new IOException(Messages.MESSAGES.unableToGetBoundary());

        if (!(genericType instanceof ParameterizedType))
            throw new IllegalArgumentException(
                    Messages.MESSAGES.receivedGenericType(this, genericType, ParameterizedType.class));

        ParameterizedType param = (ParameterizedType) genericType;
        Type baseType = param.getActualTypeArguments()[0];
        Class<?> rawType = Types.getRawType(baseType);

        MultipartInputImpl input = new MultipartInputImpl(mediaType, workers);
        input.parse(entityStream);

        List<Object> list = new ArrayList<Object>();

        for (InputPart part : input.getParts())
            list.add(part.getBody(rawType, baseType));

        if (!InputStream.class.equals(rawType)) {
            // make sure any temporary files are discarded
            input.close();
        }

        return list;
    }
}
