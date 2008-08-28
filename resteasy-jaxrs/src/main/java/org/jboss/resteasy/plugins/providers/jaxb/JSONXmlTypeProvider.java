/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.resteasy.plugins.providers.jaxb;

import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

/**
 * A JSONXmlTypeProvider.
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
public class JSONXmlTypeProvider extends JAXBXmlTypeProvider
{

   /**
    * 
    */
   protected XMLStreamReader getXMLStreamReader(InputStream entityStream)
   {
      return XMLStreamFactory.getBadgerFishXMLStreamReader(entityStream);
   }

   /**
    * 
    */
   protected XMLStreamWriter getXMLStreamWriter(OutputStream entityStream)
   {
      return XMLStreamFactory.getBadgerFishXMLStreamWriter(entityStream);
   }
}
