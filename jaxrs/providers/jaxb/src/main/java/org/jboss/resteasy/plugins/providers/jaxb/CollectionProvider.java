package org.jboss.resteasy.plugins.providers.jaxb;

import org.jboss.resteasy.annotations.providers.jaxb.DoNotUseJAXBProvider;
import org.jboss.resteasy.annotations.providers.jaxb.Wrapped;
import org.jboss.resteasy.spi.ResteasyConfiguration;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.FindAnnotation;
import org.jboss.resteasy.util.Types;
import org.w3c.dom.Element;
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
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
@Produces({"application/*+xml", "text/*+xml"})
@Consumes({"application/*+xml", "text/*+xml"})
public class CollectionProvider implements MessageBodyReader<Object>, MessageBodyWriter<Object>
{
   @Context
   protected Providers providers;
   private boolean expandEntityReferences = true;
   
   public CollectionProvider()
   {
      ResteasyConfiguration context = ResteasyProviderFactory.getContextData(ResteasyConfiguration.class);
      if (context != null)
      {
         String s = context.getParameter("resteasy.document.expand.entity.references");
         if (s != null)
         {
            setExpandEntityReferences(Boolean.parseBoolean(s));
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
      if ((Collection.class.isAssignableFrom(type) || type.isArray()) && genericType != null)
      {
         Class baseType = Types.getCollectionBaseType(type, genericType);
         if (baseType == null) return false;
         return (baseType.isAnnotationPresent(XmlRootElement.class) || baseType.isAnnotationPresent(XmlType.class) || baseType.isAnnotationPresent(XmlSeeAlso.class) || JAXBElement.class.equals(baseType)) && (FindAnnotation.findAnnotation(baseType, annotations, DoNotUseJAXBProvider.class) == null) && !IgnoredMediaTypes.ignored(baseType, annotations, mediaType);
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
      Class baseType = Types.getCollectionBaseType(type, genericType);
      JaxbCollection col = null;
      try
      {
         JAXBElement<JaxbCollection> ele = null;
         
         if (suppressExpandEntityExpansion())
         {
            SAXSource source = new SAXSource(new InputSource(entityStream));
            JAXBContext ctx = finder.findCachedContext(JaxbCollection.class, mediaType, annotations);
            Unmarshaller unmarshaller = ctx.createUnmarshaller();
            unmarshaller = new ExternalEntityUnmarshaller(unmarshaller);
            ele = unmarshaller.unmarshal(source, JaxbCollection.class);
         }
         else
         {  
            StreamSource source = new StreamSource(entityStream);
            JAXBContext ctx = finder.findCachedContext(JaxbCollection.class, mediaType, annotations);
            ele = ctx.createUnmarshaller().unmarshal(source, JaxbCollection.class);
         }
      
         Wrapped wrapped = FindAnnotation.findAnnotation(annotations, Wrapped.class);
         if (wrapped != null)
         {
            if (!wrapped.element().equals(ele.getName().getLocalPart()))
            {
               throw new JAXBUnmarshalException("Collection wrapping failed, expected root element name of " + wrapped.element() + " got " + ele.getName().getLocalPart());
            }
            if (!wrapped.namespace().equals(ele.getName().getNamespaceURI()))
            {
               throw new JAXBUnmarshalException("Collection wrapping failed, expect namespace of " + wrapped.namespace() + " got " + ele.getName().getNamespaceURI());
            }
         }

         col = ele.getValue();
      }
      catch (JAXBException e)
      {
         throw new JAXBUnmarshalException(e);
      }

      try
      {
         JAXBContext ctx = finder.findCachedContext(baseType, mediaType, null);
         Unmarshaller unmarshaller = ctx.createUnmarshaller();
         unmarshaller = AbstractJAXBProvider.decorateUnmarshaller(baseType, annotations, mediaType, unmarshaller);
         if (type.isArray())
         {
            Object array = Array.newInstance(baseType, col.getValue().size());
            for (int i = 0; i < col.getValue().size(); i++)
            {
               Element val = (Element) col.getValue().get(i);
               Array.set(array, i, unmarshaller.unmarshal(val));
            }
            return array;
         }
         else
         {
            Collection outCol = null;
            if (type.isInterface())
            {
               if (List.class.isAssignableFrom(type)) outCol = new ArrayList();
               else if (Set.class.isAssignableFrom(type)) outCol = new HashSet();
               else outCol = new ArrayList();
            }
            else
            {
               try
               {
                  outCol = (Collection) type.newInstance();
               }
               catch (Exception e)
               {
                  throw new JAXBUnmarshalException(e);
               }
            }
            for (Object obj : col.getValue())
            {
               Element val = (Element) obj;
               outCol.add(unmarshaller.unmarshal(val));
            }
            return outCol;
         }
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

   public void writeTo(Object entry, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException
   {
      JAXBContextFinder finder = getFinder(mediaType);
      if (finder == null)
      {
         throw new JAXBMarshalException("Unable to find JAXBContext for media type: " + mediaType);
      }
      Class baseType = Types.getCollectionBaseType(type, genericType);
      try
      {
         JAXBContext ctx = finder.findCacheContext(mediaType, annotations, JaxbCollection.class, baseType);
         JaxbCollection col = new JaxbCollection();
         if (type.isArray())
         {
            Object[] array = (Object[]) entry;
            for (Object obj : array)
            {
               col.getValue().add(obj);
            }
         }
         else
         {
            Collection collection = (Collection) entry;
            for (Object obj : collection) col.getValue().add(obj);
         }

         String element = "collection";
         String namespaceURI = "";
         String prefix = "";

         Wrapped wrapped = FindAnnotation.findAnnotation(annotations, Wrapped.class);
         if (wrapped != null)
         {
            element = wrapped.element();
            namespaceURI = wrapped.namespace();
            prefix = wrapped.prefix();
         }


         JAXBElement<JaxbCollection> collection = new JAXBElement<JaxbCollection>(new QName(namespaceURI, element, prefix), JaxbCollection.class, col);
         Marshaller marshaller = ctx.createMarshaller();
         AbstractJAXBProvider.decorateMarshaller(baseType, annotations, mediaType, marshaller);
         marshaller.marshal(collection, entityStream);
      }
      catch (JAXBException e)
      {
         throw new JAXBMarshalException(e);
      }
   }
   
   public boolean isExpandEntityReferences()
   {
      return expandEntityReferences;
   }

   public void setExpandEntityReferences(boolean expandEntityReferences)
   {
      this.expandEntityReferences = expandEntityReferences;
   }
   
   protected boolean suppressExpandEntityExpansion()
   {
      return !isExpandEntityReferences();
   }
}