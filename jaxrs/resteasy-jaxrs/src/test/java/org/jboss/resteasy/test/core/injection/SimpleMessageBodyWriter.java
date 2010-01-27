package org.jboss.resteasy.test.core.injection;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;

@Provider
@Produces("text/plain")
public class SimpleMessageBodyWriter implements MessageBodyWriter<String>
{

   @Context
   private Providers fieldProviders = null;
   private Providers constructorProviders = null;
   // just in case there was a pool of instances - we want to test all of them
   private static Set<SimpleMessageBodyWriter> instances = new HashSet<SimpleMessageBodyWriter>();

   public SimpleMessageBodyWriter(@Context Providers providers)
   {
      constructorProviders = providers;
      instances.add(this);
   }

   public long getSize(String t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return 3;
   }

   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return true;
   }

   public void writeTo(String t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException
   {
      Writer writer = new OutputStreamWriter(entityStream);
      writer.write("bar");
      writer.flush();
   }

   public Providers getFieldProviders()
   {
      return fieldProviders;
   }

   public Providers getConstructorProviders()
   {
      return constructorProviders;
   }

   public static Set<SimpleMessageBodyWriter> getInstances()
   {
      return instances;
   }
}
