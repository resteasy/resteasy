package org.jboss.resteasy.test.providers.resource;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.MessageBodyWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Produces(MediaType.APPLICATION_ATOM_XML)
@Consumes(MediaType.APPLICATION_ATOM_XML)
public class ContractsDataReaderWriter implements MessageBodyReader<ContractsData>, MessageBodyWriter<ContractsData> {
   @Override
   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return type.equals(ContractsData.class);
   }

   @Override
   public ContractsData readFrom(Class<ContractsData> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
      return null;
   }

   @Override
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return type.equals(ContractsData.class);
   }

   @Override
   public long getSize(ContractsData data, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
      return 0;
   }

   @Override
   public void writeTo(ContractsData data, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {

   }
}
