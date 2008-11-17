package org.jboss.resteasy.plugins.providers.jaxb;

import org.jboss.resteasy.annotations.providers.jaxb.JAXBConfig;
import org.jboss.resteasy.util.FindAnnotation;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.lang.annotation.Annotation;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
@Produces({"text/*+xml", "application/*+xml"})
public class XmlJAXBContextFinder extends AbstractJAXBContextFinder implements ContextResolver<JAXBContextFinder>
{
   private ConcurrentHashMap<Class<?>, JAXBContext> cache = new ConcurrentHashMap<Class<?>, JAXBContext>();
   private ConcurrentHashMap<CacheKey, JAXBContext> collectionCache = new ConcurrentHashMap<CacheKey, JAXBContext>();


   public JAXBContext findCachedContext(Class type, MediaType mediaType, Annotation[] parameterAnnotations) throws JAXBException
   {
      JAXBContext result;

      JAXBContext jaxb = cache.get((Class<?>) type);
      if (jaxb != null) result = jaxb;
      jaxb = findProvidedJAXBContext((Class<?>) type, mediaType);
      if (jaxb != null)
      {
         cache.putIfAbsent((Class<?>) type, jaxb);
         result = jaxb;
      }
      jaxb = createContext(parameterAnnotations, type);
      if (jaxb != null) cache.putIfAbsent((Class<?>) type, jaxb);
      result = jaxb;
      return result;
   }

   protected JAXBContext createContextObject(Annotation[] parameterAnnotations, Class... classes) throws JAXBException
   {
      JAXBConfig config = FindAnnotation.findAnnotation(parameterAnnotations, JAXBConfig.class);
      return new JAXBContextWrapper(config, classes);
   }

   public JAXBContext findCacheContext(MediaType mediaType, Annotation[] paraAnnotations, Class... classes) throws JAXBException
   {
      CacheKey key = new CacheKey(classes);
      JAXBContext ctx = collectionCache.get(key);
      if (ctx != null) return ctx;

      ctx = createContextObject(paraAnnotations, classes);
      collectionCache.put(key, ctx);

      return ctx;
   }
}
