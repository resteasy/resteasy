package org.jboss.resteasy.plugins.providers.json.jettison;

import org.jboss.resteasy.plugins.providers.jaxb.JAXBElementProvider;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class JsonJAXBElementProvider extends JAXBElementProvider
{
   @Override
   protected JAXBContext findJAXBContext(Class<?> type) throws JAXBException
   {
      return super.findJAXBContext(type);
   }
}
