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
    * a ContextResolver{@literal <}JAXBContext{@literal >} for the given media type.
    *
    * @param type type
    * @param mediaType media type
    * @param parameterAnnotations annotations
    * @return {@link JAXBContext}
    * @throws JAXBException jaxb exception
    */
   JAXBContext findCachedContext(Class type, MediaType mediaType, Annotation[] parameterAnnotations) throws JAXBException;

   /**
    * This method creates a JAXBContext from a collection of classes.  Unlike the other findCachedContext() method,
    * the user cannot override the JAXBContext created.
    *
    * @param mediaType media type
    * @param paraAnnotations annotations
    * @param classes classes
    * @return {@link JAXBContext}
    * @throws JAXBException jaxb exception
    */
   JAXBContext findCacheContext(MediaType mediaType, Annotation[] paraAnnotations, Class... classes) throws JAXBException;

   /**
    * This method will find a JAXBContext from a set of XmlTypes that use an ObjectFactory for creation (i.e. from xjc).
    *
    * @param mediaType media type
    * @param paraAnnotations annotations
    * @param classes classes
    * @return {@link JAXBContext}
    * @throws JAXBException jaxb exception
    */
   JAXBContext findCacheXmlTypeContext(MediaType mediaType, Annotation[] paraAnnotations, Class... classes) throws JAXBException;

   JAXBContext createContext(Annotation[] parameterAnnotations, Class... classes) throws JAXBException;
}
