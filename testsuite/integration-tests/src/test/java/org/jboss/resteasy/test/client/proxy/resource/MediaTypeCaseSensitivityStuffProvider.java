package org.jboss.resteasy.test.client.proxy.resource;

import org.jboss.resteasy.plugins.providers.ProviderHelper;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
@Consumes("appLication/stUff")
public class MediaTypeCaseSensitivityStuffProvider implements MessageBodyReader<MediaTypeCaseSensitivityStuff> {
   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return type.equals(MediaTypeCaseSensitivityStuff.class);
   }

   public MediaTypeCaseSensitivityStuff readFrom(Class<MediaTypeCaseSensitivityStuff> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
      return new MediaTypeCaseSensitivityStuff(ProviderHelper.readString(entityStream, mediaType));
   }
}
