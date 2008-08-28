/*
 * JBoss, the OpenSource J2EE webOS Distributable under LGPL license. See terms of license at gnu.org.
 */
package org.jboss.resteasy.plugins.providers.jaxb;

import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.ext.Provider;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

/**
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
@Provider
@Produces("application/fastinfoset")
@Consumes("application/fastinfoset")
public class FastinfoSetXmlTypeProvider extends JAXBXmlTypeProvider
{

   /**
    * 
    */
   protected XMLStreamReader getXMLStreamReader(InputStream entityStream)
   {
      return XMLStreamFactory.getFastinfoSetXMLStreamReader(entityStream);
   }

   /**
    * 
    */
   protected XMLStreamWriter getXMLStreamWriter(OutputStream entityStream)
   {
      return XMLStreamFactory.getFastinfoSetXMLStreamWriter(entityStream);
   }

}
