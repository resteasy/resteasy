package org.resteasy.plugins.providers.json.jettison;

import org.codehaus.jettison.mapped.MappedNamespaceConvention;
import org.resteasy.util.FindAnnotation;

import javax.ws.rs.ConsumeMime;
import javax.ws.rs.ProduceMime;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Reader/Writer for JAXB annotated classes and JSON.  Uses <a href="http://jettison.codehaus.org">Jettison</a>
 * <p/>
 * Supports Badger or Mapped convention.  Badger is the default convention.  You can override this default by
 * using the @Mapped/@Badged annotation on the JAXB class or parameter you are (un)marshalling.  Putting it on a parameter
 * will always override any annotation on the class.
 * <p/>
 * This class creates and caches separate class specific badger and mapped JAXBContexts.
 *
 * @author <a href="mailto:bburke@redhat.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
@ProduceMime("application/json")
@ConsumeMime("application/json")
public class JettisonProvider implements MessageBodyReader<Object>, MessageBodyWriter<Object>
{
   private ConcurrentHashMap<Class<?>, JAXBContext> badgerCache = new ConcurrentHashMap<Class<?>, JAXBContext>();
   private ConcurrentHashMap<Class<?>, JAXBContext> mappedCache = new ConcurrentHashMap<Class<?>, JAXBContext>();

   private MappedNamespaceConvention mappedNamespaceConvention;

   public void setMappedNamespaceConvention(MappedNamespaceConvention mappedNamespaceConvention)
   {
      this.mappedNamespaceConvention = mappedNamespaceConvention;
   }

   /**
    * This provider caches JAXBContext's on a per class basis.  You can use this method to clear, prune, or prepopulate
    * the cache with your own JAXBContext instances.
    * <p/>
    * You can obtain an instance of this class by doing ResteasyProviderFactory.getProvider(JettisonProvider.class);
    */
   public Map<Class<?>, JAXBContext> getBadgerCache()
   {
      return badgerCache;
   }

   /**
    * This provider caches JAXBContext's on a per class basis.  You can use this method to clear, prune, or prepopulate
    * the cache with your own JAXBContext instances.
    * <p/>
    * You can obtain an instance of this class by doing ResteasyProviderFactory.getProvider(JettisonProvider.class);
    */
   public Map<Class<?>, JAXBContext> getMappedCache()
   {
      return badgerCache;
   }

   /**
    * Lookup.  Will create context if it doesn't exist.  Might be useful for prepopulating cache.
    *
    * @param clazz
    * @return
    * @throws javax.xml.bind.JAXBException
    */
   public JAXBContext getBadgerContext(Class<?> clazz) throws JAXBException
   {
      JAXBContext context = badgerCache.get(clazz);
      if (context == null)
      {
         context = new BadgerContext(clazz);
         badgerCache.putIfAbsent(clazz, context);
      }
      return context;
   }

   /**
    * Lookup.  Will create context if it doesn't exist.  Might be useful for prepopulating cache.
    *
    * @param clazz
    * @return
    * @throws javax.xml.bind.JAXBException
    */
   public JAXBContext getMappedContext(Class<?> clazz) throws JAXBException
   {
      JAXBContext context = mappedCache.get(clazz);
      if (context == null)
      {
         if (mappedNamespaceConvention != null) context = new JettisonMappedContext(mappedNamespaceConvention, clazz);
         else context = new JettisonMappedContext(clazz);
         mappedCache.putIfAbsent(clazz, context);
      }
      return context;
   }

   protected JAXBContext findContext(Class<?> clazz, Annotation[] annotations) throws JAXBException
   {
      if (FindAnnotation.findAnnotation(annotations, Badger.class) != null) return getBadgerContext(clazz);
      if (FindAnnotation.findAnnotation(annotations, Mapped.class) != null) return getMappedContext(clazz);
      if (clazz.isAnnotationPresent(Badger.class)) return getBadgerContext(clazz);
      if (clazz.isAnnotationPresent(Mapped.class)) return getMappedContext(clazz);
      return getBadgerContext(clazz);
   }

   /**
    * Clear JAXBContext cache
    */
   public void clearCache()
   {
      badgerCache.clear();
      mappedCache.clear();
   }

   public boolean isReadable(Class<?> aClass, Type genericType, Annotation[] annotations)
   {
      return aClass.isAnnotationPresent(XmlRootElement.class);
   }

   public Object readFrom(Class<Object> aClass, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream inputStream) throws IOException
   {
      try
      {
         JAXBContext jaxb = findContext(aClass, annotations);
         Object obj = jaxb.createUnmarshaller().unmarshal(inputStream);

         if (obj instanceof JAXBElement)
            obj = ((JAXBElement) obj).getValue();

         return obj;
      }
      catch (JAXBException e)
      {
         throw new RuntimeException(e);
      }
   }

   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations)
   {
      return type.isAnnotationPresent(XmlRootElement.class);
   }

   public long getSize(Object object)
   {
      return -1;
   }

   public void writeTo(Object object, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream outputStream) throws IOException
   {
      try
      {
         JAXBContext jaxb = findContext(type, annotations);
         jaxb.createMarshaller().marshal(object, outputStream);
      }
      catch (JAXBException e)
      {
         throw new RuntimeException(e);
      }
   }

}