/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.resteasy.plugins.providers;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.ConsumeMime;
import javax.ws.rs.ProduceMime;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.annotations.FormValues;
import org.jboss.resteasy.annotations.FormParam;
import org.jboss.resteasy.core.ExceptionAdapter;
import org.jboss.resteasy.core.LoggerCategories;
import org.jboss.resteasy.util.TypeConverter;
import org.slf4j.Logger;

/**
 * A FormUrlEncodedObjectProvider.
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
@Provider
@ProduceMime("application/x-www-form-urlencoded")
@ConsumeMime("application/x-www-form-urlencoded")
public class FormUrlEncodedObjectProvider extends AbstractEntityProvider<Object>
{

   private static final Logger logger = LoggerCategories.getProviderLogger();

   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations)
   {
      return type.isAnnotationPresent(FormValues.class);
   }

   public Object readFrom(Class<Object> type,
                          Type genericType,
                          Annotation[] annotations,
                          MediaType mediaType,
                          MultivaluedMap<String, String> httpHeaders,
                          InputStream entityStream) throws IOException
   {
      String formData = ProviderHelper.readString(entityStream);
      String[] keys = formData.split("&");
      Map<String, String> values = new HashMap<String, String>();
      for (String pair : keys)
      {
         int index = pair.indexOf('=');
         if (index < 0)
         {
            values.put(URLDecoder.decode(pair, "UTF-8"), null);
         }
         else if (index > 0)
         {
            values.put(URLDecoder.decode(pair.substring(0, index), "UTF-8"), 
                       URLDecoder.decode(pair.substring(index + 1), "UTF-8"));
         }
      }
      return populateEntity(values, type);
   }

   private Object populateEntity(Map<String, String> values, Class<?> type)
   {
      Object object;
      try
      {
         object = type.newInstance();
         BeanInfo beanInfo = Introspector.getBeanInfo(type);

         for (Field field : type.getDeclaredFields())
         {
            if (field.isAnnotationPresent(FormParam.class))
            {
               FormParam formParam = field.getAnnotation(FormParam.class);
               String value = values.get(formParam.value());
               logger.debug("Binding value {} to property {}", value, formParam.value());
               Object convertedValue = TypeConverter.getType(field.getType(), value);
               Method writeMethod = getProperty(field.getName(), beanInfo).getWriteMethod();
               writeMethod.invoke(object, convertedValue);
            }
         }
      }
      catch (IllegalArgumentException e)
      {
         throw new ExceptionAdapter(e);
      }
      catch (InstantiationException e)
      {
         throw new ExceptionAdapter(e);
      }
      catch (IllegalAccessException e)
      {
         throw new ExceptionAdapter(e);
      }
      catch (IntrospectionException e)
      {
         throw new ExceptionAdapter(e);
      }
      catch (InvocationTargetException e)
      {
         throw new ExceptionAdapter(e);
      }
      return object;
   }

   private Map<String, String> extractValues(Object object)
   {
      Map<String, String> values = new HashMap<String, String>();
      try
      {
         BeanInfo beanInfo = Introspector.getBeanInfo(object.getClass());
         for (Field field : object.getClass().getDeclaredFields())
         {
            if (field.isAnnotationPresent(FormParam.class))
            {
               FormParam formParam = field.getAnnotation(FormParam.class);
               Method readMethod = getProperty(field.getName(), beanInfo).getReadMethod();
               Object value = readMethod.invoke(object);
               logger.debug("Read value {} from property {}", value, formParam.value());
               values.put(formParam.value(), value.toString());
            }
         }
      }
      catch (SecurityException e)
      {
         throw new ExceptionAdapter(e);
      }
      catch (IllegalArgumentException e)
      {
         throw new ExceptionAdapter(e);
      }
      catch (IntrospectionException e)
      {
         throw new ExceptionAdapter(e);
      }
      catch (IllegalAccessException e)
      {
         throw new ExceptionAdapter(e);
      }
      catch (InvocationTargetException e)
      {
         throw new ExceptionAdapter(e);
      }
      return values;
   }

   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations)
   {
      return type.isAnnotationPresent(FormValues.class);
   }

   /**
    * FIXME Comment this
    * 
    * @param name
    * @param beanInfo
    * @return
    */
   private PropertyDescriptor getProperty(String name, BeanInfo beanInfo)
   {
      for (PropertyDescriptor property : beanInfo.getPropertyDescriptors())
      {
         if (property.getName().equals(name))
         {
            return property;
         }
      }
      return null;
   }

   /**
    * 
    */
   public void writeTo(Object t,
                       Class<?> type,
                       Type genericType,
                       Annotation[] annotations,
                       MediaType mediaType,
                       MultivaluedMap<String, Object> httpHeaders,
                       OutputStream entityStream) throws IOException
   {
      OutputStreamWriter writer = new OutputStreamWriter(entityStream, "UTF-8");
      Map<String, String> values = extractValues(t);
      boolean first = true;
      for (Map.Entry<String, String> entry : values.entrySet())
      {
         if (first)
         {
            first = false;
         }
         else
         {
            writer.write("&");
         }
         writer.write(entry.getKey());
         writer.write("=");
         writer.write(entry.getValue());
      }
      writer.flush();
   }

}
