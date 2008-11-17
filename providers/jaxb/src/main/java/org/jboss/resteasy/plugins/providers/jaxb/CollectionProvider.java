package org.jboss.resteasy.plugins.providers.jaxb;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;
import org.jboss.resteasy.annotations.providers.jaxb.Wrapped;
import org.jboss.resteasy.spi.LoggableFailure;
import org.jboss.resteasy.util.FindAnnotation;
import org.jboss.resteasy.util.Types;
import org.w3c.dom.Element;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
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
@Produces("*/*")
@Consumes("*/*")
public class CollectionProvider implements MessageBodyReader<Object>, MessageBodyWriter<Object>
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
      if (FindAnnotation.findAnnotation(annotations, Wrapped.class) != null && (Collection.class.isAssignableFrom(type) || type.isArray()))
      {
         return true;
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
         throw new LoggableFailure("Unable to find JAXBContext for media type: " + mediaType, Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
      }
      Class baseType = Types.getCollectionBaseType(type, genericType);
      JaxbCollection col = null;
      try
      {
         JAXBContext ctx = finder.findCachedContext(JaxbCollection.class, mediaType, annotations);
         col = (JaxbCollection) ctx.createUnmarshaller().unmarshal(entityStream);
      }
      catch (JAXBException e)
      {
         throw new LoggableFailure(e);
      }

      try
      {
         JAXBContext ctx = finder.findCachedContext(baseType, mediaType, null);
         Unmarshaller unmarshaller = ctx.createUnmarshaller();
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
                  throw new LoggableFailure(e);
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
         throw new LoggableFailure(e);
      }
   }

   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      if (FindAnnotation.findAnnotation(annotations, Wrapped.class) != null && (Collection.class.isAssignableFrom(type) || type.isArray()))
      {
         return true;
      }
      return false;
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
         throw new LoggableFailure("Unable to find JAXBContext for media type: " + mediaType, Response.Status.INTERNAL_SERVER_ERROR.getStatusCode());
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
         NamespacePrefixMapper mapper = new NamespacePrefixMapper()
         {
            public String getPreferredPrefix(String namespace, String s1, boolean b)
            {
               if (namespace.equals("http://jboss.org/resteasy")) return "resteasy";
               else return s1;
            }
         };
         Marshaller marshaller = ctx.createMarshaller();
         marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", mapper);
         marshaller.marshal(col, entityStream);
      }
      catch (JAXBException e)
      {
         throw new LoggableFailure(e);
      }
   }
}