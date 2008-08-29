package org.jboss.resteasy.plugins.providers.jaxb;

import javax.xml.bind.JAXBException;
import java.io.OutputStream;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface MarshallerSpi
{
   void marshal(Object o, OutputStream outputStream)
           throws JAXBException;
}
