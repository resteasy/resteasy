package org.jboss.resteasy.plugins.providers.jaxb;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.lang.annotation.Annotation;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface JAXBContextFinder
{
   /**
    * This method with find a JAXBContext for one type.  The user can override the cache by defining
    * a ContextResolver<JAXBContext> for the given media type.
    *
    * @param type
    * @param mediaType
    * @param parameterAnnotations
    * @return
    * @throws JAXBException
    */
   JAXBContext findCachedContext(Class type, MediaType mediaType, Annotation[] parameterAnnotations) throws JAXBException;

   /**
    * This method creates a JAXBContext from a collection of classes.  Unlike the other findCachedContext() method,
    * the user cannot override the JAXBContext created.
    *
    * @param mediaType
    * @param paraAnnotations
    * @param classes
    * @return
    * @throws JAXBException
    */
   JAXBContext findCacheContext(MediaType mediaType, Annotation[] paraAnnotations, Class... classes) throws JAXBException;

   /**
    * This method will find a JAXBContext from a set of XmlTypes that use an ObjectFactory for creation (i.e. from xjc)
    *
    * @param mediaType
    * @param paraAnnotations
    * @param packages
    * @return
    * @throws JAXBException
    */
   JAXBContext findCacheXmlTypeContext(MediaType mediaType, Annotation[] paraAnnotations, Class... classes) throws JAXBException;

   JAXBContext createContext(Annotation[] parameterAnnotations, Class... classes) throws JAXBException;
}
