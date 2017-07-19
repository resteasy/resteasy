package org.jboss.resteasy.plugins.providers.jaxb.json;

import org.jboss.resteasy.annotations.providers.jaxb.json.BadgerFish;
import org.jboss.resteasy.annotations.providers.jaxb.json.Mapped;
import org.jboss.resteasy.plugins.providers.jaxb.AbstractJAXBContextFinder;
import org.jboss.resteasy.plugins.providers.jaxb.JAXBContextFinder;
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
@Produces({"application/json", "application/*+json"})
public class JsonJAXBContextFinder extends AbstractJAXBContextFinder implements ContextResolver<JAXBContextFinder>
{
   private ConcurrentHashMap<Class<?>, JAXBContext> mappedCache = new ConcurrentHashMap<Class<?>, JAXBContext>();
   private ConcurrentHashMap<Class<?>, JAXBContext> badgerCache = new ConcurrentHashMap<Class<?>, JAXBContext>();
   private ConcurrentHashMap<CacheKey, JAXBContext> mappedCollectionCache = new ConcurrentHashMap<CacheKey, JAXBContext>();
   private ConcurrentHashMap<CacheKey, JAXBContext> mappedXmlTypeCollectionCache = new ConcurrentHashMap<CacheKey, JAXBContext>();
   private ConcurrentHashMap<CacheKey, JAXBContext> badgerCollectionCache = new ConcurrentHashMap<CacheKey, JAXBContext>();
   private ConcurrentHashMap<CacheKey, JAXBContext> badgerXmlTypeCollectionCache = new ConcurrentHashMap<CacheKey, JAXBContext>();

   protected JAXBContext createContextObject(Annotation[] annotations, Class... classes) throws JAXBException
   {
      Mapped mapped = FindAnnotation.findAnnotation(annotations, Mapped.class);
      BadgerFish badger = FindAnnotation.findAnnotation(annotations, BadgerFish.class);
      if (badger != null)
      {
         return new BadgerContext(classes);
      }
      else
      {
         return new JettisonMappedContext(mapped, classes);
      }
   }

   @Override
   protected JAXBContext createContextObject(Annotation[] annotations, String contextPath) throws JAXBException
   {
      Mapped mapped = FindAnnotation.findAnnotation(annotations, Mapped.class);
      BadgerFish badger = FindAnnotation.findAnnotation(annotations, BadgerFish.class);
      if (badger != null)
      {
         return new BadgerContext(contextPath);
      }
      else
      {
         return new JettisonMappedContext(mapped, contextPath);
      }
   }

   @Override
   public JAXBContext findCacheXmlTypeContext(MediaType mediaType, Annotation[] annotations, Class... classes) throws JAXBException
   {
      CacheKey key = new CacheKey(classes);
      Mapped mapped = FindAnnotation.findAnnotation(annotations, Mapped.class);
      BadgerFish badger = FindAnnotation.findAnnotation(annotations, BadgerFish.class);
      if (badger != null)
      {
         JAXBContext ctx = badgerXmlTypeCollectionCache.get(key);
         if (ctx != null) return ctx;
         ctx = createXmlTypeContext(annotations, classes);
         badgerXmlTypeCollectionCache.put(key, ctx);
         return ctx;
      }
      else
      {
         JAXBContext ctx = mappedXmlTypeCollectionCache.get(key);
         if (ctx != null) return ctx;
         ctx = createXmlTypeContext(annotations, classes);
         mappedXmlTypeCollectionCache.put(key, ctx);
         return ctx;
      }
   }

   public JAXBContext findCachedContext(Class type, MediaType mediaType, Annotation[] annotations) throws JAXBException
   {
      Mapped mapped = FindAnnotation.findAnnotation(type, annotations, Mapped.class);
      BadgerFish badger = FindAnnotation.findAnnotation(type, annotations, BadgerFish.class);
      if (badger != null)
      {
         return find(type, mediaType, badgerCache, mapped, badger);

      }
      else
      {
         return find(type, mediaType, mappedCache, mapped, badger);
      }
   }

   public JAXBContext findCacheContext(MediaType mediaType, Annotation[] annotations, Class... classes) throws JAXBException
   {
      CacheKey key = new CacheKey(classes);
      Mapped mapped = FindAnnotation.findAnnotation(annotations, Mapped.class);
      BadgerFish badger = FindAnnotation.findAnnotation(annotations, BadgerFish.class);
      if (badger != null)
      {
         JAXBContext ctx = badgerCollectionCache.get(key);
         if (ctx != null) return ctx;
         ctx = new BadgerContext(classes);
         badgerCollectionCache.put(key, ctx);
         return ctx;
      }
      else
      {
         JAXBContext ctx = mappedCollectionCache.get(key);
         if (ctx != null) return ctx;
         ctx = new JettisonMappedContext(mapped, classes);
         mappedCollectionCache.put(key, ctx);
         return ctx;
      }
   }

   protected JAXBContext find(Class<?> type, MediaType mediaType, ConcurrentHashMap<Class<?>, JAXBContext> cache, Mapped mapped, BadgerFish badger)
           throws JAXBException
   {
      JAXBContext jaxb;
      jaxb = cache.get(type);
      if (jaxb != null)
      {
         return jaxb;
      }
      jaxb = findProvidedJAXBContext(type, mediaType);
      if (jaxb == null)
      {
         if (badger != null)
         {
            jaxb = new BadgerContext(type);
         }
         else if (mapped != null)
         {
            jaxb = new JettisonMappedContext(mapped, type);
         }
         else
         {
            jaxb = new JettisonMappedContext(type);
         }
      }
      cache.putIfAbsent(type, jaxb);
      return jaxb;
   }

}
