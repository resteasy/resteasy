package org.jboss.resteasy.plugins.providers.jaxb;

import org.jboss.resteasy.annotations.providers.jaxb.DoNotUseJAXBProvider;
import org.jboss.resteasy.plugins.providers.jaxb.i18n.Messages;
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

import org.jboss.logging.Logger.Level;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;

/**
 * A JAXBXmlRootElementProvider.
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
@Provider
@Produces({"application/*+xml", "text/*+xml"})
@Consumes({"application/*+xml", "text/*+xml"})
public class JAXBXmlSeeAlsoProvider extends AbstractJAXBProvider<Object>
{
   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Call of provider : org.jboss.resteasy.plugins.providers.jaxb.JAXBXmlSeeAlsoProvider , method call : findJAXBContext .")
   public JAXBContext findJAXBContext(Class<?> type, Annotation[] annotations, MediaType mediaType, boolean reader)
           throws JAXBException
   {
      ContextResolver<JAXBContextFinder> resolver = providers.getContextResolver(JAXBContextFinder.class, mediaType);
      JAXBContextFinder finder = resolver.getContext(type);
      if (finder == null)
      {
         if (reader) throw new JAXBUnmarshalException(Messages.MESSAGES.couldNotFindJAXBContextFinder(mediaType));
         else throw new JAXBMarshalException(Messages.MESSAGES.couldNotFindJAXBContextFinder(mediaType));
      }

      XmlSeeAlso seeAlso = type.getAnnotation(XmlSeeAlso.class);
      return finder.findCacheContext(mediaType, annotations, seeAlso.value());
   }


   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Call of provider : org.jboss.resteasy.plugins.providers.jaxb.JAXBXmlSeeAlsoProvider , method call : isReadWritable .")
   protected boolean isReadWritable(Class<?> type,
                                    Type genericType,
                                    Annotation[] annotations,
                                    MediaType mediaType)
   {
      return (!type.isAnnotationPresent(XmlRootElement.class) && !type.isAnnotationPresent(XmlType.class) && type.isAnnotationPresent(XmlSeeAlso.class)) && (FindAnnotation.findAnnotation(type, annotations, DoNotUseJAXBProvider.class) == null) && !IgnoredMediaTypes.ignored(type, annotations, mediaType);
   }

}
