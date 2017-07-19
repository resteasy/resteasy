package org.jboss.resteasy.plugins.providers.jaxb.json;

import org.jboss.resteasy.plugins.providers.jaxb.AbstractJAXBProvider;
import org.jboss.resteasy.plugins.providers.jaxb.CollectionProvider;
import org.jboss.resteasy.plugins.providers.jaxb.JAXBContextFinder;
import org.jboss.resteasy.plugins.providers.jaxb.JAXBMarshalException;
import org.jboss.resteasy.plugins.providers.jaxb.JAXBUnmarshalException;
import org.jboss.resteasy.plugins.providers.jaxb.json.i18n.Messages;
import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;

import org.jboss.resteasy.util.Types;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
@Produces({"application/json", "application/*+json"})
@Consumes({"application/json", "application/*+json"})
public class JsonCollectionProvider extends CollectionProvider
{

   @SuppressWarnings("unchecked")
   public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException
   {
      LogMessages.LOGGER.debugf("Provider : %s,  Method : readFrom", getClass().getName());
      Class baseType = Types.getCollectionBaseType(type, genericType);
      Reader reader = null;
      String charset = mediaType.getParameters().get("charset");
      if (charset != null)
      {
         reader = new BufferedReader(new InputStreamReader(entityStream, charset));
      }
      else
      {
         reader = new BufferedReader(new InputStreamReader(entityStream));
      }


      char c = JsonParsing.eatWhitspace(reader, false);
      if (c != '[') throw new JAXBUnmarshalException(Messages.MESSAGES.expectingJsonArray());
      c = JsonParsing.eatWhitspace(reader, true);
      ArrayList list = new ArrayList();
      if (c != ']')
      {
         MessageBodyReader messageReader = providers.getMessageBodyReader(baseType, null, annotations, mediaType);
         LogMessages.LOGGER.debugf("MessageBodyReader: %s", messageReader.getClass().getName());

         do
         {
            String str = JsonParsing.extractJsonMapString(reader);
            ByteArrayInputStream stream = new ByteArrayInputStream(str.getBytes());
            Object obj = messageReader.readFrom(baseType, null, annotations, mediaType, httpHeaders, stream);
            list.add(obj);

            c = JsonParsing.eatWhitspace(reader, false);

            if (c == ']') break;

            if (c != ',')
            {
               throw new JAXBUnmarshalException(Messages.MESSAGES.expectingCommaJsonArray());
            }
            c = JsonParsing.eatWhitspace(reader, true);
         } while (c != -1);
      }


      if (type.isArray())
      {
         Object array = Array.newInstance(baseType, list.size());
         for (int i = 0; i < list.size(); i++)
         {
            Array.set(array, i, list.get(i));
         }
         return array;
      }
      else
      {
         Collection outCol = null;
         if (type.isInterface())
         {
            if (List.class.isAssignableFrom(type)) return list;
            else if (SortedSet.class.isAssignableFrom(type)) outCol = new TreeSet();
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
         outCol.addAll(list);
         return outCol;
      }
   }

   public void writeTo(Object entry, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException
   {
      LogMessages.LOGGER.debugf("Provider : %s,  Method : writeTo", getClass().getName());
      JAXBContextFinder finder = getFinder(mediaType);
      if (finder == null)
      {
         throw new JAXBMarshalException(Messages.MESSAGES.unableToFindJAXBContext(mediaType));
      }
      Class baseType = Types.getCollectionBaseType(type, genericType);
      entityStream.write('[');
      try
      {
         JAXBContext ctx = finder.findCachedContext(baseType, mediaType, annotations);
         Marshaller marshaller = ctx.createMarshaller();
         marshaller = AbstractJAXBProvider.decorateMarshaller(baseType, annotations, mediaType, marshaller);
         if (type.isArray())
         {
            Object[] array = (Object[]) entry;
            boolean first = true;
            for (Object obj : array)
            {
               if (first)
               {
                  first = false;
               }
               else
               {
                  entityStream.write(',');
               }
               marshaller.marshal(obj, entityStream);
            }
         }
         else
         {
            Collection collection = (Collection) entry;
            boolean first = true;
            for (Object obj : collection)
            {
               if (first)
               {
                  first = false;
               }
               else
               {
                  entityStream.write(',');
               }
               marshaller.marshal(obj, entityStream);
            }
         }
         entityStream.write(']');
      }
      catch (JAXBException e)
      {
         throw new JAXBMarshalException(e);
      }
   }
}
