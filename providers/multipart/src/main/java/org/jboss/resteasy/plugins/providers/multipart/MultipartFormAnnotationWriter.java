package org.jboss.resteasy.plugins.providers.multipart;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.annotations.providers.multipart.PartType;
import org.jboss.resteasy.util.FindAnnotation;

import javax.ws.rs.FormParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
@Produces("multipart/form-data")
public class MultipartFormAnnotationWriter extends AbstractMultipartFormDataWriter implements MessageBodyWriter
{
   public boolean isWriteable(Class type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return FindAnnotation.findAnnotation(annotations, MultipartForm.class) != null || type.isAnnotationPresent(MultipartForm.class);
   }

   public long getSize(Object o, Class type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return -1;
   }

   public void writeTo(Object obj, Class type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException
   {
      MultipartFormDataOutput multipart = new MultipartFormDataOutput();

      Class theType = type;
      while (theType != null && !theType.equals(Object.class))
      {
         getFields(theType, multipart, obj);
         theType = theType.getSuperclass();
      }

      for (Method method : type.getMethods())
      {
         if (method.isAnnotationPresent(FormParam.class) && method.getName().startsWith("get") && method.getParameterTypes().length == 0
                 && method.isAnnotationPresent(PartType.class))
         {
            FormParam param = method.getAnnotation(FormParam.class);
            Object value = null;
            try
            {
               value = method.invoke(obj);
            }
            catch (IllegalAccessException e)
            {
               throw new RuntimeException(e);
            }
            catch (InvocationTargetException e)
            {
               throw new RuntimeException(e);
            }
            PartType partType = method.getAnnotation(PartType.class);

            multipart.addFormData(param.value(), value, method.getReturnType(), method.getGenericReturnType(), MediaType.valueOf(partType.value()));
         }
      }

      write(multipart, mediaType, httpHeaders, entityStream);

   }

   protected void getFields(Class type, MultipartFormDataOutput output, Object obj)
           throws IOException
   {
      for (Field field : type.getDeclaredFields())
      {
         if (field.isAnnotationPresent(FormParam.class) && field.isAnnotationPresent(PartType.class))
         {
            field.setAccessible(true);
            FormParam param = field.getAnnotation(FormParam.class);
            Object value = null;
            try
            {
               value = field.get(obj);
            }
            catch (IllegalAccessException e)
            {
               throw new RuntimeException(e);
            }
            PartType partType = field.getAnnotation(PartType.class);

            output.addFormData(param.value(), value, field.getType(), field.getGenericType(), MediaType.valueOf(partType.value()));
         }
      }
   }


}