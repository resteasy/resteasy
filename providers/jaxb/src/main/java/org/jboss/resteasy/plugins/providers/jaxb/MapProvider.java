package org.jboss.resteasy.plugins.providers.jaxb;

import org.jboss.resteasy.annotations.providers.jaxb.DoNotUseJAXBProvider;
import org.jboss.resteasy.annotations.providers.jaxb.WrappedMap;
import org.jboss.resteasy.plugins.providers.jaxb.i18n.*;
import org.jboss.resteasy.spi.ResteasyConfiguration;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.FindAnnotation;
import org.jboss.resteasy.util.Types;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.InputSource;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
@Produces({"application/xml", "application/*+xml", "text/xml", "text/*+xml"})
@Consumes({"application/xml", "application/*+xml", "text/xml", "text/*+xml"})
public class MapProvider implements MessageBodyReader<Object>, MessageBodyWriter<Object>
{
   @Context
   protected Providers providers;
   private boolean disableExternalEntities = true;
   private boolean enableSecureProcessingFeature = true;
   private boolean disableDTDs = true;
   
   public MapProvider()
   {
      ResteasyConfiguration context = ResteasyProviderFactory.getContextData(ResteasyConfiguration.class);
      if (context != null)
      {
         String s = context.getParameter("resteasy.document.expand.entity.references");
         if (s != null)
         {
            setDisableExternalEntities(!Boolean.parseBoolean(s));
         }
         s = context.getParameter("resteasy.document.secure.processing.feature");
         if (s != null)
         {
            setEnableSecureProcessingFeature(Boolean.parseBoolean(s));
         }
         s = context.getParameter("resteasy.document.secure.disableDTDs");
         if (s != null)
         {
            setDisableDTDs(Boolean.parseBoolean(s));
         }
      }
   }
   
   protected JAXBContextFinder getFinder(MediaType type)
   {
      ContextResolver<JAXBContextFinder> resolver = providers.getContextResolver(JAXBContextFinder.class, type);
      if (resolver == null) return null;
      return resolver.getContext(null);
   }

   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return isWrapped(type, genericType, annotations, mediaType);
   }

   protected boolean isWrapped(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      if (Map.class.isAssignableFrom(type) && genericType != null)
      {
         Class<?> keyType = Types.getMapKeyType(genericType);
         if (keyType == null) return false;
         if (!CharSequence.class.isAssignableFrom(keyType) && !Number.class.isAssignableFrom(keyType)) return false;

         Class<?> valueType = Types.getMapValueType(genericType);
         if (valueType == null) return false;
         valueType = XmlAdapterWrapper.xmlAdapterValueType(valueType, annotations);
         return (valueType.isAnnotationPresent(XmlRootElement.class) || valueType.isAnnotationPresent(XmlType.class) || valueType.isAnnotationPresent(XmlSeeAlso.class) || JAXBElement.class.equals(valueType)) && (FindAnnotation.findAnnotation(valueType, annotations, DoNotUseJAXBProvider.class) == null) && !IgnoredMediaTypes.ignored(valueType, annotations, mediaType);
      }
      return false;
   }

   public Object getJAXBObject(JAXBContextFinder finder, MediaType mediaType, Class<?> clazz, Element element) throws JAXBException
   {
      JAXBContext ctx = finder.findCachedContext(clazz, mediaType, null);
      return ctx.createUnmarshaller().unmarshal(element);
   }


   public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException
   {
      LogMessages.LOGGER.debugf("Provider : %s,  Method : readFrom", getClass().getName());
      JAXBContextFinder finder = getFinder(mediaType);
      if (finder == null)
      {
         throw new JAXBUnmarshalException(Messages.MESSAGES.unableToFindJAXBContext(mediaType));
      }
      Class valueType = Types.getMapValueType(genericType);
      XmlAdapterWrapper xmlAdapter = XmlAdapterWrapper.getXmlAdapter(valueType, annotations);
      if (xmlAdapter != null)
      {
         valueType = xmlAdapter.getValueType();  
      }
      JaxbMap jaxbMap = null;
      JAXBElement<JaxbMap> ele = null;
      
      try
      {
         JAXBContext ctx = finder.findCacheContext(mediaType, annotations, JaxbMap.class, JaxbMap.Entry.class, valueType);
         if (needsSecurity())
         {
            SAXSource source = null;
            if (getCharset(mediaType) == null)
            {
               source = new SAXSource(new InputSource(new InputStreamReader(entityStream, StandardCharsets.UTF_8)));
            }
            else
            {
               source = new SAXSource(new InputSource(entityStream));
            }
            Unmarshaller unmarshaller = ctx.createUnmarshaller();
            unmarshaller = new SecureUnmarshaller(unmarshaller, disableExternalEntities, enableSecureProcessingFeature, disableDTDs);
            ele = unmarshaller.unmarshal(source, JaxbMap.class);
         }
         else
         {
            StreamSource source = null;
            if (getCharset(mediaType) == null)
            {
               source = new StreamSource(new InputStreamReader(entityStream, StandardCharsets.UTF_8));
            }
            else
            {
               source = new StreamSource(entityStream);
            }
            
            ele = ctx.createUnmarshaller().unmarshal(source, JaxbMap.class);
         }
         WrappedMap wrapped = FindAnnotation.findAnnotation(annotations, WrappedMap.class);
         if (wrapped != null)
         {
            if (!wrapped.map().equals(ele.getName().getLocalPart()))
            {
               throw new JAXBUnmarshalException(Messages.MESSAGES.mapWrappingFailedLocalPart(wrapped.map(), ele.getName().getLocalPart()));
            }
            if (!wrapped.namespace().equals(ele.getName().getNamespaceURI()))
            {
               throw new JAXBUnmarshalException(Messages.MESSAGES.mapWrappingFailedNamespace(wrapped.namespace(), ele.getName().getNamespaceURI()));
            }
         }

         jaxbMap = ele.getValue();

         HashMap<String, Object> map = new HashMap<String, Object>();

         Unmarshaller unmarshaller = ctx.createUnmarshaller();
         unmarshaller = AbstractJAXBProvider.decorateUnmarshaller(valueType, annotations, mediaType, unmarshaller);

         for (int i = 0; i < jaxbMap.getValue().size(); i++)
         {
            Element element = (Element) jaxbMap.getValue().get(i);
            NamedNodeMap attributeMap = element.getAttributes();
            String keyValue = null;
            if (wrapped != null)
            {
               keyValue = element.getAttribute(wrapped.key());

            }
            else
            {
               if (attributeMap.getLength() == 0)
                  throw new JAXBUnmarshalException(Messages.MESSAGES.mapWrappedFailedKeyAttribute());
               for (int j = 0; j < attributeMap.getLength(); j++)
               {
                  Attr key = (Attr) attributeMap.item(j);
                  if (!key.getName().startsWith("xmlns"))
                  {
                     keyValue = key.getValue();
                     break;
                  }
               }

            }


            Object value = unmarshaller.unmarshal(element.getFirstChild());
            if (xmlAdapter != null)
            {
               try
               {
                  value = xmlAdapter.unmarshal(value);
               }
               catch (Exception e)
               {
                  throw new JAXBUnmarshalException(e);
               }
            }
            map.put(keyValue, value);
         }
         return map;
      }
      catch (JAXBException e)
      {
         throw new JAXBUnmarshalException(e);
      }
   }

   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return isWrapped(type, genericType, annotations, mediaType);
   }

   public long getSize(Object entry, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return -1;
   }

   public void writeTo(Object target, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException
   {
      LogMessages.LOGGER.debugf("Provider : %s,  Method : writeTo", getClass().getName());
      JAXBContextFinder finder = getFinder(mediaType);
      if (finder == null)
      {
         throw new JAXBMarshalException(Messages.MESSAGES.unableToFindJAXBContext(mediaType));
      }
      Class valueType = Types.getMapValueType(genericType);
      XmlAdapterWrapper xmlAdapter = XmlAdapterWrapper.getXmlAdapter(valueType, annotations);
      if (xmlAdapter != null)
      {
         valueType = xmlAdapter.getValueType();
      }
      try
      {
         JAXBContext ctx = finder.findCacheContext(mediaType, annotations, JaxbMap.class, JaxbMap.Entry.class, valueType);

         String mapName = "map";
         String entryName = "entry";
         String keyName = "key";
         String namespaceURI = "";
         String prefix = "";

         WrappedMap wrapped = FindAnnotation.findAnnotation(annotations, WrappedMap.class);
         if (wrapped != null)
         {
            mapName = wrapped.map();
            entryName = wrapped.entry();
            namespaceURI = wrapped.namespace();
            prefix = wrapped.prefix();
            keyName = wrapped.key();
         }

         JaxbMap map = new JaxbMap(entryName, keyName, namespaceURI);

         @SuppressWarnings("unchecked")
		 Map<Object, Object> targetMap = (Map) target;
         for (Map.Entry mapEntry : targetMap.entrySet())
         {
            Object value = mapEntry.getValue();
            if (xmlAdapter != null)
            {
               try
               {
                  value = xmlAdapter.marshal(value);
               }
               catch (Exception e)
               {
                  throw new JAXBMarshalException(e);
               }
            }
            map.addEntry(mapEntry.getKey().toString(), value);
         }

         JAXBElement<JaxbMap> jaxbMap = new JAXBElement<JaxbMap>(new QName(namespaceURI, mapName, prefix), JaxbMap.class, map);
         Marshaller marshaller = ctx.createMarshaller();
         marshaller = AbstractJAXBProvider.decorateMarshaller(valueType, annotations, mediaType, marshaller);
         marshaller.marshal(jaxbMap, entityStream);
      }
      catch (JAXBException e)
      {
         throw new JAXBMarshalException(e);
      }
   }

   public boolean isDisableExternalEntities()
   {
      return disableExternalEntities;
   }

   public void setDisableExternalEntities(boolean disableExternalEntities)
   {
      this.disableExternalEntities = disableExternalEntities;
   }

   public boolean isEnableSecureProcessingFeature()
   {
      return enableSecureProcessingFeature;
   }

   public void setEnableSecureProcessingFeature(boolean enableSecureProcessingFeature)
   {
      this.enableSecureProcessingFeature = enableSecureProcessingFeature;
   }

   public boolean isDisableDTDs()
   {
      return disableDTDs;
   }

   public void setDisableDTDs(boolean disableDTDs)
   {
      this.disableDTDs = disableDTDs;
   }
   
   public static String getCharset(final MediaType mediaType)
   {
      if (mediaType != null)
      {
         return mediaType.getParameters().get("charset");
      }
      return null;
   }
   
   protected boolean needsSecurity()
   {
      return true;
   }
}
