package org.jboss.resteasy.test.providers.custom.resource;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
@Produces("text/json")
public class MediaTypeFromMessageBodyWriterTextJson implements MessageBodyWriter<CustomProviderPreferenceUser> {
    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return CustomProviderPreferenceUser.class.isAssignableFrom(type);
    }

    @Override
    public long getSize(CustomProviderPreferenceUser customProviderPreferenceUser, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1L;
    }

    @Override
    public void writeTo(CustomProviderPreferenceUser customProviderPreferenceUser, Class<?> type, Type genericType,
                        Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
                        OutputStream entityStream) throws IOException, WebApplicationException {
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(entityStream));
        bw.write(customProviderPreferenceUser.getUsername() + " " + customProviderPreferenceUser.getEmail());
        bw.flush();
    }
}
