package org.jboss.resteasy.test.asynch.resource;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
public class CallbackStringBeanEntityProvider implements MessageBodyReader<CallbackStringBean>,
      MessageBodyWriter<CallbackStringBean> {

   @Override
   public boolean isWriteable(Class<?> type, Type genericType,
                               Annotation[] annotations, MediaType mediaType) {
      return CallbackStringBean.class.isAssignableFrom(type);
   }

   @Override
   public long getSize(CallbackStringBean t, Class<?> type, Type genericType,
                        Annotation[] annotations, MediaType mediaType) {
      return t.get().length();
   }

   @Override
   public void writeTo(CallbackStringBean t, Class<?> type, Type genericType,
                        Annotation[] annotations, MediaType mediaType,
                        MultivaluedMap<String, Object> httpHeaders,
                        OutputStream entityStream) throws IOException,
            WebApplicationException {
      entityStream.write(t.get().getBytes());
   }

   @Override
   public boolean isReadable(Class<?> type, Type genericType,
                              Annotation[] annotations, MediaType mediaType) {
      return isWriteable(type, genericType, annotations, mediaType);
   }

   @Override
   public CallbackStringBean readFrom(Class<CallbackStringBean> type, Type genericType,
                                       Annotation[] annotations, MediaType mediaType,
                                       MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
            throws IOException, WebApplicationException {
      String stream = readFromStream(entityStream);
      CallbackStringBean bean = new CallbackStringBean(stream);
      return bean;
   }

   public static final String readFromStream(InputStream stream) throws IOException {
      InputStreamReader isr = new InputStreamReader(stream);
      return readFromReader(isr);
   }

   public static final String readFromReader(Reader reader) throws IOException {
      BufferedReader br = new BufferedReader(reader);
      String entity = br.readLine();
      br.close();
      return entity;
   }

}
