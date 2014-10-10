package org.jboss.resteasy.plugins.providers.jaxb;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.UnmarshallerHandler;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.attachment.AttachmentUnmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.Schema;

import org.jboss.resteasy.providers.jaxb.i18n.Messages;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

/**
 * 
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * 
 * @version $Revision: 1.1 $
 * Created Feb 1, 2012
 */
public class SecureUnmarshaller implements Unmarshaller {

	private Unmarshaller delegate;
	boolean disableExternalEntities;
	boolean enableSecureProcessingFeature;
	boolean disableDTDs;
	
	public SecureUnmarshaller(Unmarshaller delegate, boolean disableExternalEntities, boolean enableSecureProcessingFeature, boolean disableDTDs) {
		this.delegate = delegate;
		this.disableExternalEntities = disableExternalEntities;
		this.enableSecureProcessingFeature = enableSecureProcessingFeature;
		this.disableDTDs = disableDTDs;
	}
	
	@SuppressWarnings("unchecked")
   public <A extends XmlAdapter> A getAdapter(Class<A> type) {
		return delegate.getAdapter(type);
	}

	public AttachmentUnmarshaller getAttachmentUnmarshaller() {
	   return delegate.getAttachmentUnmarshaller();
	}
	
	public ValidationEventHandler getEventHandler() throws JAXBException {
	   return delegate.getEventHandler();
	}

   public Listener getListener() {
      return delegate.getListener();
   }

   public Object getProperty(String name) throws PropertyException {
      return delegate.getProperty(name);
   }

   public Schema getSchema() {
      return delegate.getSchema();
   }

   public UnmarshallerHandler getUnmarshallerHandler() {
      return delegate.getUnmarshallerHandler();
   }

   /**
    * @deprecated since 2.0
    */
   @Deprecated
   public boolean isValidating() throws JAXBException {
      return delegate.isValidating();
   }

   /**
    * @deprecated since 2.0
    */
   @Deprecated
   @SuppressWarnings("unchecked")
   public void setAdapter(XmlAdapter adapter) {
      delegate.setAdapter(adapter);
   }

   @SuppressWarnings("unchecked")
   public <A extends XmlAdapter> void setAdapter(Class<A> type, A adapter) {
      delegate.setAdapter(adapter);
   }

   public void setAttachmentUnmarshaller(AttachmentUnmarshaller au) {
      delegate.setAttachmentUnmarshaller(au);
   }

   public void setEventHandler(ValidationEventHandler handler)throws JAXBException {
      delegate.setEventHandler(handler);
   }

   public void setListener(Listener listener) {
      delegate.setListener(listener);
   }
   
   public void setProperty(String name, Object value) throws PropertyException {
      delegate.setProperty(name, value);
   }

   public void setSchema(Schema schema) {
      delegate.setSchema(schema);
   }

   /**
    * @deprecated since 2.0
    */
   @Deprecated
   public void setValidating(boolean validating) throws JAXBException {
      delegate.setValidating(validating);
   }

   public Object unmarshal(File f) throws JAXBException {
      throw new UnsupportedOperationException(Messages.MESSAGES.secureUnmarshallerUnexpectedUseFile());
   }

   /**
    * Turns off expansion of external entities.
    */
   public Object unmarshal(InputStream is) throws JAXBException {
      return unmarshal(new InputSource(is));
   }
   
   public Object unmarshal(Reader reader) throws JAXBException {
      throw new UnsupportedOperationException(Messages.MESSAGES.secureUnmarshallerUnexpectedUseReader());
   }

   public Object unmarshal(URL url) throws JAXBException {
      throw new UnsupportedOperationException(Messages.MESSAGES.secureUnmarshallerUnexpectedUseURL());
   }

   /**
    * Turns off expansion of external entities.
    */
   public Object unmarshal(InputSource source) throws JAXBException
   {
       try
       {
          SAXParserFactory spf = SAXParserFactory.newInstance();
          configureParserFactory(spf);
          SAXParser sp = spf.newSAXParser();
          XMLReader xmlReader = sp.getXMLReader();
          SAXSource saxSource = new SAXSource(xmlReader, source);
          return delegate.unmarshal(saxSource);
      }
      catch (SAXException e)
      {
         throw new JAXBException(e);
      }
      catch (ParserConfigurationException e)
      {
         throw new JAXBException(e);
      }
   }

   public Object unmarshal(Node node) throws JAXBException {
      return delegate.unmarshal(node);
   }

   public Object unmarshal(Source source) throws JAXBException {
      if(source instanceof SAXSource)
      {
         try
         {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            configureParserFactory(spf);
            SAXParser sp = spf.newSAXParser();
            XMLReader xmlReader = sp.getXMLReader();
            ((SAXSource) source).setXMLReader(xmlReader);
            return delegate.unmarshal(source);
         }
         catch (SAXException e)
         {
            throw new JAXBException(e);
         }
         catch (ParserConfigurationException e)
         {
            throw new JAXBException(e);
         }
      }
      
      throw new UnsupportedOperationException(Messages.MESSAGES.secureUnmarshallerUnexpectedUseSourceClass());
   }

   public Object unmarshal(XMLStreamReader reader) throws JAXBException {
      throw new UnsupportedOperationException(Messages.MESSAGES.secureUnmarshallerUnexpectedUseXMLStreamReader());
   }

   public Object unmarshal(XMLEventReader reader) throws JAXBException {
      throw new UnsupportedOperationException(Messages.MESSAGES.secureUnmarshallerUnexpectedUseXMLEventReader());
   }

   public <T> JAXBElement<T> unmarshal(Node node, Class<T> declaredType) throws JAXBException {
      throw new UnsupportedOperationException(Messages.MESSAGES.secureUnmarshallerUnexpectedUseNodeClass());
   }

   public <T> JAXBElement<T> unmarshal(Source source, Class<T> declaredType) throws JAXBException
   {
      if(source instanceof SAXSource)
      {
         try
         {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            configureParserFactory(spf);
            SAXParser sp = spf.newSAXParser();
            XMLReader xmlReader = sp.getXMLReader();
            ((SAXSource) source).setXMLReader(xmlReader);
            return delegate.unmarshal(source, declaredType);
         }
         catch (SAXException e)
         {
            throw new JAXBException(e);
         }
         catch (ParserConfigurationException e)
         {
            throw new JAXBException(e);
         }
      }
      
      throw new UnsupportedOperationException(Messages.MESSAGES.secureUnmarshallerUnexpectedUseSourceClass());
   }

   public <T> JAXBElement<T> unmarshal(XMLStreamReader reader, Class<T> declaredType) throws JAXBException {
      throw new UnsupportedOperationException(Messages.MESSAGES.secureUnmarshallerUnexpectedUseXMLStreamReaderClass());
   }

   public <T> JAXBElement<T> unmarshal(XMLEventReader reader, Class<T> declaredType) throws JAXBException {
      throw new UnsupportedOperationException(Messages.MESSAGES.secureUnmarshallerUnexpectedUseXMLEventReaderClass());
   }

   public Unmarshaller getDelegate()
   {
      return delegate;
   }

   public void setDelegate(Unmarshaller delegate)
   {
      this.delegate = delegate;
   }
   
   protected void configureParserFactory(SAXParserFactory factory) throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException
   {
      factory.setFeature("http://xml.org/sax/features/validation", false);
      factory.setFeature("http://xml.org/sax/features/namespaces", true);
      factory.setFeature("http://xml.org/sax/features/external-general-entities", !disableExternalEntities);
      factory.setFeature("http://xml.org/sax/features/external-parameter-entities", !disableExternalEntities);
      factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, enableSecureProcessingFeature); 
      factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", disableDTDs); 
   }

}
