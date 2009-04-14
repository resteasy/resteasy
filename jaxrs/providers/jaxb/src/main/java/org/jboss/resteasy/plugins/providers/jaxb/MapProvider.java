package org.jboss.resteasy.plugins.providers.jaxb;

import org.jboss.resteasy.annotations.providers.jaxb.WrappedMap;
import org.jboss.resteasy.util.FindAnnotation;
import org.jboss.resteasy.util.Types;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

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
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
@Produces("*/*")
@Consumes("*/*")
public class MapProvider implements MessageBodyReader<Object>, MessageBodyWriter<Object>
{
   @Context
   protected Providers providers;

   protected JAXBContextFinder getFinder(MediaType type)
   {
      ContextResolver<JAXBContextFinder> resolver = providers.getContextResolver(JAXBContextFinder.class, type);
      if (resolver == null) return null;
      return resolver.getContext(null);
   }

   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return isWrapped(type, genericType, annotations);
   }

   protected boolean isWrapped(Class<?> type, Type genericType, Annotation[] annotations)
   {
      if (Map.class.isAssignableFrom(type) && genericType != null)
      {
         Class keyType = Types.getMapKeyType(genericType);
         if (keyType == null) return false;
         if (!keyType.equals(String.class)) return false;

         Class valueType = Types.getMapValueType(genericType);
         if (valueType == null) return false;
         return valueType.isAnnotationPresent(XmlRootElement.class) || valueType.isAnnotationPresent(XmlType.class) || valueType.isAnnotationPresent(XmlSeeAlso.class) || JAXBElement.class.equals(type);
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
      JAXBContextFinder finder = getFinder(mediaType);
      if (finder == null)
      {
         throw new JAXBUnmarshalException("Unable to find JAXBContext for media type: " + mediaType);
      }
      Class valueType = Types.getMapValueType(genericType);
      JaxbMap jaxbMap = null;
      try
      {
         StreamSource source = new StreamSource(entityStream);
         JAXBContext ctx = finder.findCacheContext(mediaType, annotations, JaxbMap.class, JaxbMap.Entry.class, valueType);
         JAXBElement<JaxbMap> ele = ctx.createUnmarshaller().unmarshal(source, JaxbMap.class);

         WrappedMap wrapped = FindAnnotation.findAnnotation(annotations, WrappedMap.class);
         if (wrapped != null)
         {
            if (!wrapped.map().equals(ele.getName().getLocalPart()))
            {
               throw new JAXBUnmarshalException("Map wrapping failed, expected root element name of " + wrapped.map() + " got " + ele.getName().getLocalPart());
            }
            if (!wrapped.namespace().equals(ele.getName().getNamespaceURI()))
            {
               throw new JAXBUnmarshalException("Map wrapping failed, expect namespace of " + wrapped.namespace() + " got " + ele.getName().getNamespaceURI());
            }
         }

         jaxbMap = ele.getValue();

         HashMap map = new HashMap();

         Unmarshaller unmarshaller = ctx.createUnmarshaller();

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
                  throw new JAXBUnmarshalException("Map wrapped failed, could not find map entry key attribute");
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
      return isWrapped(type, genericType, annotations);
   }

   public long getSize(Object entry, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return -1;
   }

   public void writeTo(Object target, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException
   {
      JAXBContextFinder finder = getFinder(mediaType);
      if (finder == null)
      {
         throw new JAXBMarshalException("Unable to find JAXBContext for media type: " + mediaType);
      }
      Class valueType = Types.getMapValueType(genericType);
      try
      {
         JAXBContext ctx = finder.findCacheContext(mediaType, annotations, JaxbMap.class, JaxbMap.Entry.class, valueType);

         String mapName = "map";
         String entryName = "entry";
         String keyName = "key";
         String namespaceURI = "http://jboss.org/resteasy";
         String prefix = "resteasy";

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

         Map<Object, Object> targetMap = (Map) target;
         for (Map.Entry mapEntry : targetMap.entrySet())
         {
            map.addEntry(mapEntry.getKey().toString(), mapEntry.getValue());
         }

         JAXBElement<JaxbMap> jaxbMap = new JAXBElement<JaxbMap>(new QName(namespaceURI, mapName, prefix), JaxbMap.class, map);
         Marshaller marshaller = ctx.createMarshaller();
         marshaller.marshal(jaxbMap, entityStream);
      }
      catch (JAXBException e)
      {
         throw new JAXBMarshalException(e);
      }
   }
}