package org.jboss.resteasy.test.core.interceptors.resource;

import javax.annotation.Priority;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.WriterInterceptor;
import jakarta.ws.rs.ext.WriterInterceptorContext;
import java.io.IOException;
import java.util.LinkedList;

@Provider
@Priority(100)
public class ReaderContextFirstWriterInterceptor implements WriterInterceptor {

   @Override
   public void aroundWriteTo(WriterInterceptorContext context)
         throws IOException, WebApplicationException {
      MultivaluedMap<String, Object> headers = context.getHeaders();
      String header = (String) headers.getFirst(ReaderContextResource.HEADERNAME);
      if (header != null && header.equals(getClass().getName())) {
         context.setAnnotations(ReaderContextResource.class.getAnnotations());
         context.setEntity(toList(getClass().getName()));
         context.setMediaType(MediaType.TEXT_HTML_TYPE);
         context.setType(LinkedList.class);
      }
      context.proceed();
   }

   private static <T> LinkedList<T> toList(T o) {
      LinkedList<T> list = new LinkedList<T>();
      list.add(o);
      return list;
   }
}
