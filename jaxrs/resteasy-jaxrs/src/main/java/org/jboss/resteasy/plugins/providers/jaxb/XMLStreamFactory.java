/*
 * JBoss, the OpenSource J2EE webOS Distributable under LGPL license. See terms of license at gnu.org.
 */
package org.jboss.resteasy.plugins.providers.jaxb;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.codehaus.jettison.badgerfish.BadgerFishXMLStreamReader;
import org.codehaus.jettison.badgerfish.BadgerFishXMLStreamWriter;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.codehaus.jettison.mapped.MappedNamespaceConvention;
import org.codehaus.jettison.mapped.MappedXMLStreamReader;
import org.codehaus.jettison.mapped.MappedXMLStreamWriter;
import org.jboss.resteasy.core.ExceptionAdapter;
import org.jboss.resteasy.plugins.providers.ProviderHelper;

import com.sun.xml.fastinfoset.stax.StAXDocumentParser;
import com.sun.xml.fastinfoset.stax.StAXDocumentSerializer;

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

   /**
    * 
    */
   protected static XMLStreamReader getFastinfoSetXMLStreamReader(InputStream entityStream)
   {
      InputStream in = new BufferedInputStream(entityStream, 2048);
      XMLStreamReader streamReader = new StAXDocumentParser(in);
      return streamReader;
   }

   /**
    * 
    */
   protected static XMLStreamWriter getFastinfoSetXMLStreamWriter(OutputStream entityStream)
   {
      BufferedOutputStream out = new BufferedOutputStream(entityStream, 2048);
      XMLStreamWriter writer = new StAXDocumentSerializer(out);
      return writer;
   }

   /**
    * FIXME Comment this
    * 
    * @param entityStream
    * @return
    */
   protected static XMLStreamReader getMappedXMLStreamReader(InputStream entityStream)
   {
      try
      {
         String jsonString = ProviderHelper.readString(entityStream);
         XMLStreamReader streamReader = new MappedXMLStreamReader(new JSONObject(jsonString));
         return streamReader;
      }
      catch (IOException e)
      {
         throw new ExceptionAdapter(e);
      }
      catch (JSONException e)
      {
         throw new ExceptionAdapter(e);
      }
      catch (XMLStreamException e)
      {
         throw new ExceptionAdapter(e);
      }
   }
   
   
   protected static XMLStreamWriter getMappedXMLStreamWriter(OutputStream entityStream)
   {
      OutputStreamWriter out = new OutputStreamWriter(entityStream);
      MappedNamespaceConvention convention = new MappedNamespaceConvention();
      XMLStreamWriter writer = new MappedXMLStreamWriter(convention, out);
      return writer;
   }
   /**
    * 
    */
   protected static XMLStreamWriter getBadgerFishXMLStreamWriter(OutputStream entityStream)
   {
      OutputStreamWriter out = new OutputStreamWriter(entityStream);
      XMLStreamWriter writer = new BadgerFishXMLStreamWriter(out);
      return writer;
   }
   /**
    * 
    */
   protected static XMLStreamReader getBadgerFishXMLStreamReader(InputStream entityStream)
   {
      try
      {
         String jsonString = ProviderHelper.readString(entityStream);
         XMLStreamReader streamReader = new BadgerFishXMLStreamReader(new JSONObject(jsonString));
         return streamReader;
      }
      catch (IOException e)
      {
         throw new ExceptionAdapter(e);
      }
      catch (JSONException e)
      {
         throw new ExceptionAdapter(e);
      }
      catch (XMLStreamException e)
      {
         throw new ExceptionAdapter(e);
      }
   }

   

}
