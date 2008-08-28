/*
 * JBoss, the OpenSource J2EE webOS Distributable under LGPL license. See terms of license at gnu.org.
 */
package org.jboss.resteasy.plugins.providers.json;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.validation.Schema;

import org.codehaus.jettison.badgerfish.BadgerFishXMLStreamWriter;
import org.codehaus.jettison.mapped.MappedNamespaceConvention;
import org.codehaus.jettison.mapped.MappedXMLStreamWriter;
import org.jboss.resteasy.annotations.JSONConvention;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;

/**
 * A JSONMarshaller.
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
public class JSONMarshaller implements Marshaller
{
   private JSONJAXBContextWrapper context;

   private Marshaller delegateMarshaller;

   protected JSONMarshaller(JSONJAXBContextWrapper context) throws JAXBException
   {
      this.context = context;
      this.delegateMarshaller = this.context.createMarshaller();

   }

   @SuppressWarnings("unchecked")
   public <A extends XmlAdapter> A getAdapter(Class<A> type)
   {
      return delegateMarshaller.getAdapter(type);
   }

   public AttachmentMarshaller getAttachmentMarshaller()
   {
      return delegateMarshaller.getAttachmentMarshaller();
   }

   public ValidationEventHandler getEventHandler() throws JAXBException
   {
      return delegateMarshaller.getEventHandler();
   }

   public Listener getListener()
   {
      return delegateMarshaller.getListener();
   }

   public Node getNode(Object contentTree) throws JAXBException
   {
      return delegateMarshaller.getNode(contentTree);
   }

   public Object getProperty(String name) throws PropertyException
   {
      return delegateMarshaller.getProperty(name);
   }

   public Schema getSchema()
   {
      return delegateMarshaller.getSchema();
   }

   public void marshal(Object jaxbElement, Result result) throws JAXBException
   {
      delegateMarshaller.marshal(jaxbElement, result);
   }

   public void marshal(Object jaxbElement, OutputStream os) throws JAXBException
   {
      Writer writer = new OutputStreamWriter(os);
      marshal(jaxbElement, writer);

   }

   /**
    * 
    */
   public void marshal(Object jaxbElement, File output) throws JAXBException
   {
      try
      {
         FileOutputStream out = new FileOutputStream(output);
         marshal(jaxbElement, out);
      }
      catch (FileNotFoundException e)
      {
         throw new JAXBException(e);
      }
   }

   public void marshal(Object jaxbElement, Writer writer) throws JAXBException
   {
      delegateMarshaller.marshal(jaxbElement, getXMLStreamWriter(writer));
   }

   public void marshal(Object jaxbElement, ContentHandler handler) throws JAXBException
   {
      delegateMarshaller.marshal(jaxbElement, handler);

   }

   public void marshal(Object jaxbElement, Node node) throws JAXBException
   {
      delegateMarshaller.marshal(jaxbElement, node);
   }

   public void marshal(Object jaxbElement, XMLStreamWriter writer) throws JAXBException
   {
      delegateMarshaller.marshal(jaxbElement, writer);
   }

   public void marshal(Object jaxbElement, XMLEventWriter writer) throws JAXBException
   {
      delegateMarshaller.marshal(jaxbElement, writer);
   }

   /**
    * FIXME Comment this
    * 
    * @param writer
    * @return
    */
   private XMLStreamWriter getXMLStreamWriter(Writer writer)
   {
      if (context.getConvention().equals(JSONConvention.MAPPED))
      {
         MappedNamespaceConvention convention = new MappedNamespaceConvention(context.getConfiguration());
         return new MappedXMLStreamWriter(convention, writer);
      }
      return new BadgerFishXMLStreamWriter(writer);
   }

   @SuppressWarnings("unchecked")
   public void setAdapter(XmlAdapter adapter)
   {
      delegateMarshaller.setAdapter(adapter);
   }

   @SuppressWarnings("unchecked")
   public <A extends XmlAdapter> void setAdapter(Class<A> type, A adapter)
   {
      delegateMarshaller.setAdapter(adapter);
   }

   public void setAttachmentMarshaller(AttachmentMarshaller am)
   {
      delegateMarshaller.setAttachmentMarshaller(am);
   }

   public void setEventHandler(ValidationEventHandler handler) throws JAXBException
   {
      delegateMarshaller.setEventHandler(handler);
   }

   public void setListener(Listener listener)
   {
      delegateMarshaller.setListener(listener);
   }

   public void setProperty(String name, Object value) throws PropertyException
   {
      delegateMarshaller.setProperty(name, value);
   }

   public void setSchema(Schema schema)
   {
      delegateMarshaller.setSchema(schema);
   }
}
