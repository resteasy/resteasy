package org.jboss.resteasy.plugins.providers.multipart;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.plugins.providers.multipart.i18n.Messages;
import org.jboss.resteasy.spi.ReaderException;
import org.jboss.resteasy.util.FindAnnotation;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
@Consumes("multipart/form-data")
public class MultipartFormAnnotationReader implements MessageBodyReader<Object>
{
   protected
   @Context
   Providers workers;

   @Override
   public boolean isReadable(Class<?> type, Type genericType,
                             Annotation[] annotations, MediaType mediaType)
   {
      return FindAnnotation.findAnnotation(annotations, MultipartForm.class) != null
              || type.isAnnotationPresent(MultipartForm.class);
   }

   @Override
   public Object readFrom(Class<Object> type, Type genericType,
                          Annotation[] annotations, MediaType mediaType,
                          MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
           throws IOException, WebApplicationException
   {
      String boundary = mediaType.getParameters().get("boundary");
      if (boundary == null)
         throw new IOException(Messages.MESSAGES.unableToGetBoundary());
      MultipartFormDataInputImpl input = new MultipartFormDataInputImpl(
              mediaType, workers);
      input.parse(entityStream);

      Object obj;
      try
      {
         obj = type.newInstance();
      }
      catch (InstantiationException e)
      {
         throw new ReaderException(e.getCause());
      }
      catch (IllegalAccessException e)
      {
         throw new ReaderException(e);
      }

      boolean hasInputStream = false;

      Class<?> theType = type;
      while (theType != null && !theType.equals(Object.class))
      {
         if (setFields(theType, input, obj))
         {
            hasInputStream = true;
         }
         theType = theType.getSuperclass();
      }

      for (Method method : type.getMethods())
      {
         if (method.isAnnotationPresent(FormParam.class)
                 && method.getName().startsWith("set")
                 && method.getParameterTypes().length == 1)
         {
            FormParam param = method.getAnnotation(FormParam.class);
            List<InputPart> list = input.getFormDataMap()
                    .get(param.value());
            if (list == null || list.isEmpty())
               continue;
            InputPart part = list.get(0);
            // if (part == null) throw new
            // LoggableFailure("Unable to find @FormParam in multipart: " +
            // param.value());
            if (part == null)
               continue;
            Class<?> type1 = method.getParameterTypes()[0];
            Object data;
            if (InputPart.class.equals(type1)) {
                hasInputStream = true;
                data = part;
            }
            else
            {
                if (InputStream.class.equals(type1))
                {
                   hasInputStream = true;
                }
                data = part.getBody(type1,
                        method.getGenericParameterTypes()[0]);
            }
            try
            {
               method.invoke(obj, data);
            }
            catch (IllegalAccessException e)
            {
               throw new ReaderException(e);
            }
            catch (InvocationTargetException e)
            {
               throw new ReaderException(e.getCause());
            }
         }
      }
      if (!hasInputStream)
      {
         input.close();
      }
      return obj;
   }

   protected boolean setFields(Class<?> type, MultipartFormDataInputImpl input,
                            Object obj) throws IOException
   {
      boolean hasInputStream = false;
      for (Field field : type.getDeclaredFields())
      {
         if (field.isAnnotationPresent(FormParam.class))
         {
            AccessController.doPrivileged(new FieldEnablerPrivilegedAction(field));
            FormParam param = field.getAnnotation(FormParam.class);
            List<InputPart> list = input.getFormDataMap()
                    .get(param.value());
            if (list == null || list.isEmpty())
               continue;
            InputPart part = list.get(0);
            // if (part == null) throw new
            // LoggableFailure("Unable to find @FormParam in multipart: " +
            // param.value());
            if (part == null)
               continue;
            Object data;
            if (InputPart.class.equals(field.getType())) {
                hasInputStream = true;
                data = part;
            }
            else
            {
                if (InputStream.class.equals(field.getType()))
                {
                    hasInputStream = true;
                }
                data = part.getBody(field.getType(), field
                        .getGenericType());
            }
            try
            {
               field.set(obj, data);
            }
            catch (IllegalAccessException e)
            {
               throw new ReaderException(e);
            }
         }
      }
      return hasInputStream;
   }

}