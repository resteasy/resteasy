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
   JAXBContext findCachedContext(Class type, MediaType mediaType, Annotation[] parameterAnnotations) throws JAXBException;

   JAXBContext createContext(Annotation[] parameterAnnotations, Class... classes) throws JAXBException;
}
