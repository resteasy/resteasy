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
import java.util.HashSet;
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
      Class[] types = new Class[]{(Class<?>) type};
      JAXBConfig config = FindAnnotation.findAnnotation(type, parameterAnnotations, JAXBConfig.class);
      HashSet<Class> classes = new HashSet<Class>();
      for (Class type1 : types)
      {
         classes.add(type1);
         Class factory = findDefaultObjectFactoryClass(type1);
         if (factory != null) classes.add(factory);
      }
      jaxb = new JAXBContextWrapper(config, classes.toArray(new Class[classes.size()]));
      if (jaxb != null) cache.putIfAbsent((Class<?>) type, jaxb);
      result = jaxb;
      return result;
   }

   protected JAXBContext createContextObject(Annotation[] parameterAnnotations, Class... classes) throws JAXBException
   {
      JAXBConfig config = FindAnnotation.findAnnotation(parameterAnnotations, JAXBConfig.class);
      return new JAXBContextWrapper(config, classes);
   }


}
