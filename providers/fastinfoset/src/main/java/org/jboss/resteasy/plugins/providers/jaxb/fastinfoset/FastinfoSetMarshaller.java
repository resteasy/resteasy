package org.jboss.resteasy.plugins.providers.jaxb.fastinfoset;

import com.sun.xml.fastinfoset.stax.StAXDocumentSerializer;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;

import javax.xml.bind.JAXBContext;
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
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class FastinfoSetMarshaller implements Marshaller
{
   private JAXBContext context;
   private Marshaller marshaller;

   public FastinfoSetMarshaller(JAXBContext context) throws JAXBException
   {
      this.context = context;
      marshaller = this.context.createMarshaller();
   }


   protected static XMLStreamWriter getFastinfoSetXMLStreamWriter(OutputStream entityStream)
   {
      BufferedOutputStream out = new BufferedOutputStream(entityStream, 2048);
      XMLStreamWriter writer = new StAXDocumentSerializer(out);
      return writer;
   }

   public void marshal(Object o, Result result)
           throws JAXBException
   {
      throw new UnsupportedOperationException();
   }

   public void marshal(Object o, OutputStream outputStream)
           throws JAXBException
   {
      marshal(o, getFastinfoSetXMLStreamWriter(outputStream));
   }

   public void marshal(Object o, File file)
           throws JAXBException
   {
      try
      {
         OutputStream os = new FileOutputStream(file);
         try {
             marshal(o, os);
         } finally {
             os.close();
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
      throw new UnsupportedOperationException();
   }

   public void marshal(Object o, ContentHandler contentHandler)
           throws JAXBException
   {
      throw new UnsupportedOperationException();
   }

   public void marshal(Object o, Node node)
           throws JAXBException
   {
      throw new UnsupportedOperationException();
   }

   public void marshal(Object o, XMLStreamWriter xmlStreamWriter)
           throws JAXBException
   {
      marshaller.marshal(o, xmlStreamWriter);
   }

   public void marshal(Object o, XMLEventWriter xmlEventWriter)
           throws JAXBException
   {
      throw new UnsupportedOperationException();
   }

   public Node getNode(Object o)
           throws JAXBException
   {
      throw new UnsupportedOperationException();
   }

   public void setProperty(String s, Object o)
           throws PropertyException
   {
      marshaller.setProperty(s, o);
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