package org.jboss.resteasy.plugins.providers.jaxb.json;

import org.codehaus.jettison.badgerfish.BadgerFishXMLStreamWriter;
import org.jboss.resteasy.plugins.providers.jaxb.BaseMarshaller;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.PropertyException;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.attachment.AttachmentMarshaller;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;
import javax.xml.validation.Schema;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Writer;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class BadgerMarshaller extends BaseMarshaller
{
   private JAXBContext context;

   public BadgerMarshaller(JAXBContext context) throws JAXBException
   {
      this.context = context;
      marshaller = this.context.createMarshaller();
   }

   public void marshal(Object o, Result result)
           throws JAXBException
   {
      marshaller.marshal(o, result);
   }

   public void marshal(Object o, File file)
           throws JAXBException
   {
      try
      {
         FileOutputStream fos = new FileOutputStream(file);
         try
         {
            marshal(o, fos);
         }
         finally
         {
            fos.close();
         }
      }
      catch (IOException e)
      {
         throw new JAXBException(e);
      }
   }

   public void marshal(Object o, Writer writer)
           throws JAXBException
   {
      BadgerFishXMLStreamWriter badger = new BadgerFishXMLStreamWriter(writer);
      marshaller.marshal(o, badger);
   }

   public void marshal(Object o, ContentHandler contentHandler)
           throws JAXBException
   {
      marshaller.marshal(o, contentHandler);
   }

   public void marshal(Object o, Node node)
           throws JAXBException
   {
      marshaller.marshal(o, node);
   }

   public void marshal(Object o, XMLStreamWriter xmlStreamWriter)
           throws JAXBException
   {
      marshaller.marshal(o, xmlStreamWriter);
   }

   public void marshal(Object o, XMLEventWriter xmlEventWriter)
           throws JAXBException
   {
      marshaller.marshal(o, xmlEventWriter);
   }

   public Node getNode(Object o)
           throws JAXBException
   {
      return marshaller.getNode(o);
   }

   public Object getProperty(String s)
           throws PropertyException
   {
      return marshaller.getProperty(s);
   }

   public void setEventHandler(ValidationEventHandler validationEventHandler)
           throws JAXBException
   {
      marshaller.setEventHandler(validationEventHandler);
   }

   public ValidationEventHandler getEventHandler()
           throws JAXBException
   {
      return marshaller.getEventHandler();
   }

   public void setAdapter(XmlAdapter xmlAdapter)
   {
      marshaller.setAdapter(xmlAdapter);
   }

   public <A extends XmlAdapter> void setAdapter(Class<A> aClass, A a)
   {
      marshaller.setAdapter(aClass, a);
   }

   public <A extends XmlAdapter> A getAdapter(Class<A> aClass)
   {
      return marshaller.getAdapter(aClass);
   }

   public void setAttachmentMarshaller(AttachmentMarshaller attachmentMarshaller)
   {
      marshaller.setAttachmentMarshaller(attachmentMarshaller);
   }

   public AttachmentMarshaller getAttachmentMarshaller()
   {
      return marshaller.getAttachmentMarshaller();
   }

   public void setSchema(Schema schema)
   {
      marshaller.setSchema(schema);
   }

   public Schema getSchema()
   {
      return marshaller.getSchema();
   }

   public void setListener(Listener listener)
   {
      marshaller.setListener(listener);
   }

   public Listener getListener()
   {
      return marshaller.getListener();
   }
}
