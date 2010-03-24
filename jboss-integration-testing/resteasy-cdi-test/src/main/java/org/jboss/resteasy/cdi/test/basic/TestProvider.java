package org.jboss.resteasy.cdi.test.basic;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.inject.Inject;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;

import org.jboss.resteasy.cdi.test.Cat;
import org.jboss.resteasy.cdi.test.Dog;

@Provider
@Produces("text/plain")
public class TestProvider implements MessageBodyWriter<Dog>
{

   @Inject
   private Cat cat;
   private Cat constructorCat;
   private Cat initializerCat;
   @Context
   private Providers providers;
   
   public TestProvider()
   {
   }

   @Inject
   public TestProvider(Cat cat)
   {
      constructorCat = cat;
   }
   
   @Inject
   public void init(Cat cat)
   {
      initializerCat = cat;
   }
   
   public long getSize(Dog t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return -1;
   }

   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return type.isAssignableFrom(Dog.class);
   }

   public void writeTo(Dog t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException
   {
      BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(entityStream));
      bw.write("CDI field injection: " + (cat != null));
      bw.write("\nCDI constructor injection: " + (constructorCat != null));
      bw.write("\nCDI initializer injection: " + (initializerCat != null));
      bw.write("\nJAX-RS field injection: " + (providers != null));
      bw.write("\nProvider toString(): " + toString());
      bw.flush();
   }
}
