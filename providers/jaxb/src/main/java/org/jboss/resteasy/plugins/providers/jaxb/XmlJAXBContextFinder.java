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
@Produces({"text/xml", "text/*+xml", "application/xml", "application/*+xml"})
public class XmlJAXBContextFinder extends AbstractJAXBContextFinder implements ContextResolver<JAXBContextFinder>
{
   private ConcurrentHashMap<Class<?>, JAXBContext> cache = new ConcurrentHashMap<Class<?>, JAXBContext>();
   private ConcurrentHashMap<CacheKey, JAXBContext> collectionCache = new ConcurrentHashMap<CacheKey, JAXBContext>();
   private ConcurrentHashMap<CacheKey, JAXBContext> xmlTypeCollectionCache = new ConcurrentHashMap<CacheKey, JAXBContext>();


	@Override
   public JAXBContext findCachedContext(Class type, MediaType mediaType, Annotation[] parameterAnnotations) throws JAXBException
   {
		JAXBContext jaxb = findProvidedJAXBContext(type, mediaType);
		if (jaxb != null)
      {
			return jaxb;
      }
		jaxb = type != null ? cache.get(type) : null;
		if (jaxb == null)
      {
			jaxb = createContext(parameterAnnotations, type);
			if (jaxb != null && type != null) {
				cache.putIfAbsent(type, jaxb);
			}
      }
		return jaxb;
   }

   protected JAXBContext createContextObject(Annotation[] parameterAnnotations, Class... classes) throws JAXBException
   {
      JAXBConfig config = FindAnnotation.findAnnotation(parameterAnnotations, JAXBConfig.class);
      return new JAXBContextWrapper(config, classes);
   }

   @Override
   protected JAXBContext createContextObject(Annotation[] parameterAnnotations, String contextPath) throws JAXBException
   {
      JAXBConfig config = FindAnnotation.findAnnotation(parameterAnnotations, JAXBConfig.class);
      return new JAXBContextWrapper(contextPath, config);
   }

   public JAXBContext findCacheContext(MediaType mediaType, Annotation[] paraAnnotations, Class... classes) throws JAXBException
   {
      CacheKey key = new CacheKey(classes);
      JAXBContext ctx = collectionCache.get(key);
      if (ctx != null) return ctx;

      ctx = createContext(paraAnnotations, classes);
      collectionCache.put(key, ctx);

      return ctx;
   }

   @Override
   public JAXBContext findCacheXmlTypeContext(MediaType mediaType, Annotation[] paraAnnotations, Class... classes) throws JAXBException
   {
      CacheKey key = new CacheKey(classes);
      JAXBContext ctx = xmlTypeCollectionCache.get(key);
      if (ctx != null) return ctx;

      ctx = createXmlTypeContext(paraAnnotations, classes);
      xmlTypeCollectionCache.put(key, ctx);

      return ctx;
   }
}
