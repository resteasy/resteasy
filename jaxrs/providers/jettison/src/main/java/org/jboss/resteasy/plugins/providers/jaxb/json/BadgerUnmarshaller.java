package org.jboss.resteasy.plugins.providers.jaxb.json;

import org.codehaus.jettison.badgerfish.BadgerFishXMLStreamReader;
import org.codehaus.jettison.json.JSONObject;
import org.codehaus.jettison.json.JSONTokener;
import org.jboss.resteasy.plugins.providers.jaxb.json.i18n.Messages;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.UnmarshallerHandler;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class BadgerUnmarshaller implements Unmarshaller
{
   private Unmarshaller unmarshaller;

   public BadgerUnmarshaller(JAXBContext context) throws JAXBException
   {
      unmarshaller = context.createUnmarshaller();
   }

   public Object unmarshal(File file)
           throws JAXBException
   {
      try
      {
         return unmarshal(new FileInputStream(file));
      }
      catch (FileNotFoundException e)
      {
         throw new JAXBException(e);
      }
   }

   public Object unmarshal(InputStream inputStream)
           throws JAXBException
   {
      return unmarshal(new InputStreamReader(inputStream));
   }

   public Object unmarshal(Reader reader)
           throws JAXBException
   {
      BadgerFishXMLStreamReader badger = null;
      badger = getBadgerFishReader(reader);
      return unmarshaller.unmarshal(badger);
   }

   protected BadgerFishXMLStreamReader getBadgerFishReader(Reader reader)
           throws JAXBException
   {
      BadgerFishXMLStreamReader badger;
      char[] buffer = new char[100];
      StringBuffer buf = new StringBuffer();
      BufferedReader bufferedReader = new BufferedReader(reader);

      try
      {
         int wasRead = 0;
         do
         {
            wasRead = bufferedReader.read(buffer, 0, 100);
            if (wasRead > 0) buf.append(buffer, 0, wasRead);
         } while (wasRead > -1);
         badger = new BadgerFishXMLStreamReader(new JSONObject(new JSONTokener(buf.toString())));

      }
      catch (Exception e)
      {
         throw new JAXBException(e);
      }
      return badger;
   }

   public Object unmarshal(URL url)
           throws JAXBException
   {
      try
      {
         return unmarshal(url.openStream());
      }
      catch (IOException e)
      {
         throw new JAXBException(e);
      }
   }

   public Object unmarshal(InputSource inputSource)
           throws JAXBException
   {
      return unmarshaller.unmarshal(inputSource);
   }

   public Object unmarshal(Node node)
           throws JAXBException
   {
      return unmarshaller.unmarshal(node);
   }

   public <T> JAXBElement<T> unmarshal(Node node, Class<T> tClass)
           throws JAXBException
   {
      return unmarshaller.unmarshal(node, tClass);
   }

   public Object unmarshal(Source source)
           throws JAXBException
   {
      if (!(source instanceof StreamSource)) throw new UnsupportedOperationException(Messages.MESSAGES.expectingStreamSource());
      StreamSource stream = (StreamSource) source;
      XMLStreamReader reader = getBadgerFishReader(new InputStreamReader(stream.getInputStream()));
      return unmarshal(reader);
   }

   public <T> JAXBElement<T> unmarshal(Source source, Class<T> tClass)
           throws JAXBException
   {
      if (!(source instanceof StreamSource)) throw new UnsupportedOperationException(Messages.MESSAGES.expectingStreamSource());
      StreamSource stream = (StreamSource) source;
      XMLStreamReader reader = getBadgerFishReader(new InputStreamReader(stream.getInputStream()));
      return unmarshal(reader, tClass);
   }

   public Object unmarshal(XMLStreamReader xmlStreamReader)
           throws JAXBException
   {
      return unmarshaller.unmarshal(xmlStreamReader);
   }

   public <T> JAXBElement<T> unmarshal(XMLStreamReader xmlStreamReader, Class<T> tClass)
           throws JAXBException
   {
      return unmarshaller.unmarshal(xmlStreamReader, tClass);
   }

   public Object unmarshal(XMLEventReader xmlEventReader)
           throws JAXBException
   {
      return unmarshaller.unmarshal(xmlEventReader);
   }

   public <T> JAXBElement<T> unmarshal(XMLEventReader xmlEventReader, Class<T> tClass)
           throws JAXBException
   {
      return unmarshaller.unmarshal(xmlEventReader, tClass);
   }

   public UnmarshallerHandler getUnmarshallerHandler()
   {
      return unmarshaller.getUnmarshallerHandler();
   }

   @SuppressWarnings("deprecation")
   public void setValidating(boolean b)
           throws JAXBException
   {
      unmarshaller.setValidating(b);
   }

   @SuppressWarnings("deprecation")
   public boolean isValidating()
           throws JAXBException
   {
      return unmarshaller.isValidating();
   }

   public void setEventHandler(ValidationEventHandler validationEventHandler)
           throws JAXBException
   {
      unmarshaller.setEventHandler(validationEventHandler);
   }

   public ValidationEventHandler getEventHandler()
           throws JAXBException
   {
      return unmarshaller.getEventHandler();
   }

   public void setProperty(String s, Object o)
           throws PropertyException
   {
      unmarshaller.setProperty(s, o);
   }

   public Object getProperty(String s)
           throws PropertyException
   {
      return unmarshaller.getProperty(s);
   }

   public void setSchema(Schema schema)
   {
      unmarshaller.setSchema(schema);
   }

   public Schema getSchema()
   {
      return unmarshaller.getSchema();
   }

   public void setAdapter(XmlAdapter xmlAdapter)
   {
      unmarshaller.setAdapter(xmlAdapter);
   }

   public <A extends XmlAdapter> void setAdapter(Class<A> aClass, A a)
   {
      unmarshaller.setAdapter(aClass, a);
   }

   public <A extends XmlAdapter> A getAdapter(Class<A> aClass)
   {
      return unmarshaller.getAdapter(aClass);
   }

   public void setAttachmentUnmarshaller(AttachmentUnmarshaller attachmentUnmarshaller)
   {
      unmarshaller.setAttachmentUnmarshaller(attachmentUnmarshaller);
   }

   public AttachmentUnmarshaller getAttachmentUnmarshaller()
   {
      return unmarshaller.getAttachmentUnmarshaller();
   }

   public void setListener(Listener listener)
   {
      unmarshaller.setListener(listener);
   }

   public Listener getListener()
   {
      return unmarshaller.getListener();
   }
}
