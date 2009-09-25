/*
 * JBoss, the OpenSource J2EE webOS Distributable under LGPL license. See terms of license at gnu.org.
 */
package org.jboss.resteasy.plugins.providers.jaxb;

import org.jboss.resteasy.core.ExceptionAdapter;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * A XMLStreamFactory.
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
final class XMLStreamFactory
{

   private XMLStreamFactory()
   {

   }

   /**
    * FIXME Comment this
    *
    * @param out
    * @return
    */
   public static XMLStreamWriter getXMLStreamWriter(OutputStream out)
   {
      try
      {
         return XMLOutputFactory.newInstance().createXMLStreamWriter(out);
      }
      catch (XMLStreamException e)
      {
         throw new ExceptionAdapter(e);
      }
   }

   /**
    * FIXME Comment this
    *
    * @param entityStream
    * @return
    */
   static XMLStreamReader getXMLStreamReader(InputStream entityStream)
   {
      InputStream in = new BufferedInputStream(entityStream, 2048);
      try
      {
         XMLInputFactory factory = XMLInputFactory.newInstance();
         return factory.createXMLStreamReader(in);
      }
      catch (XMLStreamException e)
      {
         throw new ExceptionAdapter(e);
      }
   }


}
