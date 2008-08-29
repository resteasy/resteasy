package org.jboss.resteasy.plugins.providers.jaxb;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.transform.Source;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface UnmarshallerSpi
{
   <T> JAXBElement<T> unmarshal(Source source, Class<T> tClass)
           throws JAXBException;
}
