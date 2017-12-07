package org.jboss.resteasy.plugins.providers.jaxb;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

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
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.Schema;

import org.jboss.resteasy.plugins.providers.jaxb.i18n.Messages;
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
	
   private static class SAXParserProvider
   {
      private static final Map<ClassLoader, SAXParserProvider> saxParserProviders = Collections.synchronizedMap(new WeakHashMap<>());
      private final SAXParserFactory[] factories = new SAXParserFactory[8];
      
      private SAXParserProvider()
      {
         //NOOP
      }
      
      public static SAXParserProvider getInstance()
      {
         ClassLoader tccl = Thread.currentThread().getContextClassLoader();
         SAXParserProvider spp;
         spp = saxParserProviders.get(tccl);
         if (spp == null)
         {
            spp = new SAXParserProvider();
            SAXParserProvider s = saxParserProviders.putIfAbsent(tccl, spp);
            if (s != null) spp = s;
         }
         return spp;
      }
      
      public SAXParser getParser(boolean disableExternalEntities, boolean enableSecureProcessingFeature, boolean disableDTDs) throws ParserConfigurationException, SAXException
      {
         int index = (disableExternalEntities ? 1 : 0) | (enableSecureProcessingFeature ? 1 << 1 : 0) | (disableDTDs ? 1 << 2 : 0);
         SAXParserFactory f = factories[index];
         if (f == null)
         {
            f = SAXParserFactory.newInstance();
            configureParserFactory(f, disableExternalEntities, enableSecureProcessingFeature, disableDTDs);
            factories[index] = f;
         }
         SAXParser sp = f.newSAXParser();
         configParser(sp, disableExternalEntities);
         return sp;
      }
   }

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
    * @deprecated This method is deprecated as of JAXB 2.0 - please use the new
     * {@link #getSchema()} API.
    */
   @Deprecated
   public boolean isValidating() throws JAXBException {
      return delegate.isValidating();
   }

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
    * @deprecated since JAXB2.0, please see {@link #getSchema()}
    */
   @Deprecated
   public void setValidating(boolean validating) throws JAXBException {
      delegate.setValidating(validating);
   }

   public Object unmarshal(File f) throws JAXBException {
      throw new UnsupportedOperationException(Messages.MESSAGES.unexpectedUse("File"));
   }

   /**
    * Turns off expansion of external entities.
    */
   public Object unmarshal(InputStream is) throws JAXBException {
      return unmarshal(new InputSource(is));
   }
   
   public Object unmarshal(Reader reader) throws JAXBException {
      throw new UnsupportedOperationException(Messages.MESSAGES.unexpectedUse("Reader"));
   }

   public Object unmarshal(URL url) throws JAXBException {
      throw new UnsupportedOperationException(Messages.MESSAGES.unexpectedUse("URL"));
   }

   /**
    * Turns off expansion of external entities.
    */
   public Object unmarshal(InputSource source) throws JAXBException
   {
       try
       {
          SAXParser sp = SAXParserProvider.getInstance().getParser(disableExternalEntities, enableSecureProcessingFeature, disableDTDs);
          XMLReader xmlReader = sp.getXMLReader();
          final SAXSource saxSource = new SAXSource(xmlReader, source);
          if (System.getSecurityManager() == null) {
             return delegate.unmarshal(saxSource);
          }
          else
          {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<Object>()
            {
               @Override
               public Object run() throws JAXBException
               {
                  return delegate.unmarshal(saxSource);
               }
            });
          }
      }
      catch (SAXException e)
      {
         throw new JAXBException(e);
      }
      catch (ParserConfigurationException e)
      {
         throw new JAXBException(e);
      }
      catch (PrivilegedActionException pae)
      {
         throw new JAXBException(pae);
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
            SAXParser sp = SAXParserProvider.getInstance().getParser(disableExternalEntities, enableSecureProcessingFeature, disableDTDs);
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

      throw new UnsupportedOperationException(Messages.MESSAGES.unexpectedUse("Source, Class<T>"));
   }

   private static void configParser(SAXParser sp, boolean disableExternalEntities) {
      try {
         if (!disableExternalEntities)
            sp.setProperty("http://javax.xml.XMLConstants/property/accessExternalDTD", "all");
      } catch (SAXException e)
      {
         //expected, jaxp 1.5 not supported
      }
   }


   public Object unmarshal(XMLStreamReader reader) throws JAXBException {
      throw new UnsupportedOperationException(Messages.MESSAGES.unexpectedUse("XMLStreamReader"));
   }

   public Object unmarshal(XMLEventReader reader) throws JAXBException {
      throw new UnsupportedOperationException(Messages.MESSAGES.unexpectedUse("XMLEventReader"));
   }

   public <T> JAXBElement<T> unmarshal(Node node, Class<T> declaredType) throws JAXBException {
      throw new UnsupportedOperationException(Messages.MESSAGES.unexpectedUse("Node, Class<T>"));
   }

   public <T> JAXBElement<T> unmarshal(Source source, Class<T> declaredType) throws JAXBException
   {
      if(source instanceof SAXSource)
      {
         try
         {
            SAXParser sp = SAXParserProvider.getInstance().getParser(disableExternalEntities, enableSecureProcessingFeature, disableDTDs);
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

      throw new UnsupportedOperationException(Messages.MESSAGES.unexpectedUse("Source, Class<T>"));
   }

   public <T> JAXBElement<T> unmarshal(XMLStreamReader reader, Class<T> declaredType) throws JAXBException {
      throw new UnsupportedOperationException(Messages.MESSAGES.unexpectedUse("XMLStreamReader, Class<T>"));
   }

   public <T> JAXBElement<T> unmarshal(XMLEventReader reader, Class<T> declaredType) throws JAXBException {
      throw new UnsupportedOperationException(Messages.MESSAGES.unexpectedUse("XMLEventReader, Class<T>"));
   }

   public Unmarshaller getDelegate()
   {
      return delegate;
   }

   public void setDelegate(Unmarshaller delegate)
   {
      this.delegate = delegate;
   }

   protected static void configureParserFactory(SAXParserFactory factory, boolean disableExternalEntities, boolean enableSecureProcessingFeature, boolean disableDTDs) throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException
   {
      factory.setFeature("http://xml.org/sax/features/validation", false);
      factory.setFeature("http://xml.org/sax/features/namespaces", true);
      factory.setFeature("http://xml.org/sax/features/external-general-entities", !disableExternalEntities);
      factory.setFeature("http://xml.org/sax/features/external-parameter-entities", !disableExternalEntities);
      factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, enableSecureProcessingFeature);
      factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", disableDTDs); 
   }
}
