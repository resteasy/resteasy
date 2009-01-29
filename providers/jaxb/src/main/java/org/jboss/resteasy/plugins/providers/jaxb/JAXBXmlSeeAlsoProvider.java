/*
 * JBoss, the OpenSource J2EE webOS Distributable under LGPL license. See terms of license at gnu.org.
 */
package org.jboss.resteasy.plugins.providers.jaxb;

import org.jboss.resteasy.annotations.providers.jaxb.DoNotUseJAXBProvider;
import org.jboss.resteasy.spi.LoggableFailure;
import org.jboss.resteasy.util.FindAnnotation;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * A JAXBXmlRootElementProvider.
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
@Provider
@Produces("*/*")
@Consumes("*/*")
public class JAXBXmlSeeAlsoProvider extends AbstractJAXBProvider<Object>
{
   @Override
   public JAXBContext findJAXBContext(Class<?> type, Annotation[] annotations, MediaType mediaType)
           throws JAXBException
   {
      ContextResolver<JAXBContextFinder> resolver = providers.getContextResolver(JAXBContextFinder.class, mediaType);
      JAXBContextFinder finder = resolver.getContext(type);
      if (finder == null) throw new LoggableFailure("Could not find JAXBContextFinder for media type: " + mediaType);

      XmlSeeAlso seeAlso = type.getAnnotation(XmlSeeAlso.class);
      return finder.findCacheContext(mediaType, annotations, seeAlso.value());
   }


   @Override
   protected boolean isReadWritable(Class<?> type,
                                    Type genericType,
                                    Annotation[] annotations,
                                    MediaType mediaType)
   {
      return (!type.isAnnotationPresent(XmlRootElement.class) && !type.isAnnotationPresent(XmlType.class) && type.isAnnotationPresent(XmlSeeAlso.class)) && FindAnnotation.findAnnotation(type, annotations, DoNotUseJAXBProvider.class) == null;
   }

}