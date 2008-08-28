/*
 * JBoss, the OpenSource J2EE webOS Distributable under LGPL license. See terms of license at gnu.org.
 */
package org.jboss.resteasy.plugins.providers.json;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

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
import javax.xml.validation.Schema;

import org.w3c.dom.Node;
import org.xml.sax.InputSource;

/**
 * A JSONUnmarshaller.
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
public class JSONUnmarshaller implements Unmarshaller
{

   private Unmarshaller delegateUnmarshaller;

   private JSONJAXBContextWrapper context;

   /**
    * 
    * Create a new JSONUnmarshaller.
    * 
    * @param context
    * @throws JAXBException
    */
   public JSONUnmarshaller(JSONJAXBContextWrapper context) throws JAXBException
   {
      this.context = context;
      this.delegateUnmarshaller = this.context.createUnmarshaller();
   }

   @SuppressWarnings("unchecked")
   public <A extends XmlAdapter> A getAdapter(Class<A> type)
   {
      return delegateUnmarshaller.getAdapter(type);
   }

   public AttachmentUnmarshaller getAttachmentUnmarshaller()
   {
      return delegateUnmarshaller.getAttachmentUnmarshaller();
   }

   public ValidationEventHandler getEventHandler() throws JAXBException
   {
      return delegateUnmarshaller.getEventHandler();
   }

   public Listener getListener()
   {
      return delegateUnmarshaller.getListener();
   }

   public Object getProperty(String name) throws PropertyException
   {
      return delegateUnmarshaller.getProperty(name);
   }

   public Schema getSchema()
   {
      return delegateUnmarshaller.getSchema();
   }

   public UnmarshallerHandler getUnmarshallerHandler()
   {
      return delegateUnmarshaller.getUnmarshallerHandler();
   }

   @SuppressWarnings("deprecation")
   public boolean isValidating() throws JAXBException
   {
      return delegateUnmarshaller.isValidating();
   }

   @SuppressWarnings("unchecked")
   public void setAdapter(XmlAdapter adapter)
   {
      delegateUnmarshaller.setAdapter(adapter);

   }

   @SuppressWarnings("unchecked")
   public <A extends XmlAdapter> void setAdapter(Class<A> type, A adapter)
   {
      // FIXME setAdapter

   }

   public void setAttachmentUnmarshaller(AttachmentUnmarshaller au)
   {
      delegateUnmarshaller.setAttachmentUnmarshaller(au);

   }

   public void setEventHandler(ValidationEventHandler handler) throws JAXBException
   {
      delegateUnmarshaller.setEventHandler(handler);
   }

   public void setListener(Listener listener)
   {
      delegateUnmarshaller.setListener(listener);

   }

   public void setProperty(String name, Object value) throws PropertyException
   {
      delegateUnmarshaller.setProperty(name, value);
   }

   public void setSchema(Schema schema)
   {
      delegateUnmarshaller.setSchema(schema);
   }

   @SuppressWarnings("deprecation")
   public void setValidating(boolean validating) throws JAXBException
   {
      delegateUnmarshaller.setValidating(validating);
   }

   public Object unmarshal(File f) throws JAXBException
   {
      // FIXME unmarshal
      return null;
   }

   public Object unmarshal(InputStream is) throws JAXBException
   {
      // FIXME unmarshal
      return null;
   }

   public Object unmarshal(Reader reader) throws JAXBException
   {
      // FIXME unmarshal
      return null;
   }

   public Object unmarshal(URL url) throws JAXBException
   {
      // FIXME unmarshal
      return null;
   }

   public Object unmarshal(InputSource source) throws JAXBException
   {
      // FIXME unmarshal
      return null;
   }

   public Object unmarshal(Node node) throws JAXBException
   {
      // FIXME unmarshal
      return null;
   }

   public Object unmarshal(Source source) throws JAXBException
   {
      // FIXME unmarshal
      return null;
   }

   public Object unmarshal(XMLStreamReader reader) throws JAXBException
   {
      // FIXME unmarshal
      return null;
   }

   public Object unmarshal(XMLEventReader reader) throws JAXBException
   {
      // FIXME unmarshal
      return null;
   }

   public <T> JAXBElement<T> unmarshal(Node node, Class<T> declaredType) throws JAXBException
   {
      // FIXME unmarshal
      return null;
   }

   public <T> JAXBElement<T> unmarshal(Source source, Class<T> declaredType) throws JAXBException
   {
      // FIXME unmarshal
      return null;
   }

   public <T> JAXBElement<T> unmarshal(XMLStreamReader reader, Class<T> declaredType) throws JAXBException
   {
      // FIXME unmarshal
      return null;
   }

   public <T> JAXBElement<T> unmarshal(XMLEventReader reader, Class<T> declaredType) throws JAXBException
   {
      // FIXME unmarshal
      return null;
   }

}
