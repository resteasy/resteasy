package org.jboss.resteasy.plugins.providers.jaxb.json;

import org.jboss.resteasy.annotations.providers.jaxb.json.BadgerFish;
import org.jboss.resteasy.annotations.providers.jaxb.json.Mapped;
import org.jboss.resteasy.plugins.providers.jaxb.AbstractJAXBProvider;
import org.jboss.resteasy.util.FindAnnotation;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.lang.annotation.Annotation;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class JettisonJAXBContextFactory
{
   private ConcurrentHashMap<Class<?>, JAXBContext> mappedCache = new ConcurrentHashMap<Class<?>, JAXBContext>();
   private ConcurrentHashMap<Class<?>, JAXBContext> badgerCache = new ConcurrentHashMap<Class<?>, JAXBContext>();
   private AbstractJAXBProvider provider;

   public JettisonJAXBContextFactory(AbstractJAXBProvider provider)
   {
      this.provider = provider;
   }

   public JAXBContext findJAXBContext(Class<?> type, Annotation[] annotations, MediaType mediaType)
           throws JAXBException
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

   protected JAXBContext find(Class<?> type, MediaType mediaType, ConcurrentHashMap<Class<?>, JAXBContext> cache, Mapped mapped, BadgerFish badger)
           throws JAXBException
   {
      JAXBContext jaxb;
      jaxb = cache.get(type);
      if (jaxb != null)
      {
         return jaxb;
      }
      jaxb = provider.findProvidedJAXBContext(type, mediaType);
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
