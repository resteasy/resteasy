package org.jboss.resteasy.plugins.providers.jsonb;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Priority;
import javax.json.bind.Jsonb;
import javax.ws.rs.Consumes;
import javax.ws.rs.Priorities;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.jboss.resteasy.plugins.providers.jsonb.i18n.Messages;
import org.jboss.resteasy.spi.ResteasyConfiguration;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.FindAnnotation;
import org.jboss.resteasy.util.Types;

/**
 * Created by rsearls on 6/26/17.
 */
@Provider
@Produces({"application/json", "application/*+json", "text/json", "*/*"})
@Consumes({"application/json", "application/*+json", "text/json", "*/*"})
@Priority(Priorities.USER-100)
public class JsonBindingProvider extends AbstractJsonBindingProvider
        implements MessageBodyReader<Object>, MessageBodyWriter<Object> {
   private static final boolean JSONB_DISABLED = Boolean.getBoolean("resteasy.jsonb.disable");
   @Override
   public boolean isReadable(Class<?> type, Type genericType,
                             Annotation[] annotations, MediaType mediaType) {
      ResteasyConfiguration context = ResteasyProviderFactory.getContextData(ResteasyConfiguration.class);
      if (context != null && Boolean.parseBoolean(context.getParameter("resteasy.jsonb.disable")))
      {
         return false;
      }
      if (JSONB_DISABLED)
      {
         return false;
      }
      if (context != null && Boolean.parseBoolean(context.getParameter("resteasy.jsonp.enable")))
      {
         return false;
      }

      if (annotations != null && hasJacksonJaxbAnnotation(annotations))
      {
         return false;
      }
      Class<?> classType = getGenericClass(type, genericType);
      if (classType != null && isJaxbClass(classType) && isJaxbClass(classType))
      {
         return false;
      }

      Annotation[] annos = this.getAllAnnotations(classType);
      if (hasJacksonJaxbAnnotation(annos))
      {
         return false;
      }
      //supper class
      if (classType.getSuperclass() != Object.class)
      {
         Class<?> superClass = classType.getSuperclass();
         while (superClass != null)
         {
            if (hasJacksonJaxbAnnotation(getAllAnnotations(superClass)))
            {
               return false;
            }
            superClass = superClass.getSuperclass();
         }
      }
      return (isSupportedMediaType(mediaType))
            && ((!isJaxbClass(type)) || (FindAnnotation.findJsonBindingAnnotations(annotations).length != 0));
   }

   @Override
   public Object readFrom(Class<Object> type, Type genericType,
                                 Annotation[] annotations, MediaType mediaType,
                                 MultivaluedMap<String, String> httpHeaders,
                                 InputStream entityStream) throws java.io.IOException, javax.ws.rs.WebApplicationException {
      Jsonb jsonb = getJsonb(type);
      try
      {
         return jsonb.fromJson(entityStream, genericType);
      } catch (Throwable e)
      {
         // detail text provided in logger message
         throw new ProcessingException(Messages.MESSAGES.jsonBDeserializationError(e));
      }
   }

   @Override
   public boolean isWriteable(Class<?> type, Type genericType,
                              Annotation[] annotations, MediaType mediaType) {
      ResteasyConfiguration context = ResteasyProviderFactory.getContextData(ResteasyConfiguration.class);
      if (context != null && Boolean.parseBoolean(context.getParameter("resteasy.jsonb.disable")))
      {
         return false;
      }
      if (JSONB_DISABLED)
      {
         return false;
      }
      if (context != null && Boolean.parseBoolean(context.getParameter("resteasy.jsonp.enable")))
      {
         return false;
      }
      if (annotations != null && hasJacksonJaxbAnnotation(annotations))
      {
         return false;
      }
      Class<?> classType = getGenericClass(type, genericType);
      if (isJaxbClass(classType))
      {
         return false;
      }

      Annotation[] annos = this.getAllAnnotations(classType);
      if (hasJacksonJaxbAnnotation(getAllAnnotations(classType)))
      {
         return false;
      }
      //supper class
      if (classType.getSuperclass() != Object.class)
      {
         Class<?> superClass = classType.getSuperclass();
         while (superClass != null && superClass!= Object.class)
         {
            if (hasJacksonJaxbAnnotation(getAllAnnotations(superClass)))
            {
               return false;
            }
            superClass = superClass.getSuperclass();
         }
      }
      return (isSupportedMediaType(mediaType))
            && ((!isJaxbClass(type)) || (FindAnnotation.findJsonBindingAnnotations(annotations).length != 0));
   }

   @Override
   public long getSize(Object t, Class<?> type, Type genericType, Annotation[] annotations,
                       MediaType mediaType) {
      return -1L;
   }

   @Override
   public void writeTo(Object t, Class<?> type, Type genericType, Annotation[] annotations,
                       MediaType mediaType,
                       MultivaluedMap<String, Object> httpHeaders,
                       OutputStream entityStream)
           throws java.io.IOException, javax.ws.rs.WebApplicationException {
      Jsonb jsonb = getJsonb(type);
      try
      {
         entityStream.write(jsonb.toJson(t).getBytes(getCharset(mediaType)));
         entityStream.flush();
      } catch (Throwable e)
      {
         throw new ProcessingException(Messages.MESSAGES.jsonBSerializationError(e.toString()));
      }
   }
   
   public Class<?>  getGenericClass(Class<?> type, Type genericType)
   {
      Class<?> valueType = null;
      if (Map.class.isAssignableFrom(type) && genericType != null)
      {
         valueType = Types.getMapValueType(genericType);
      }

      if ((Collection.class.isAssignableFrom(type) || type.isArray()) && genericType != null)
      {
         valueType = Types.getCollectionBaseType(type, genericType);
      }
      return valueType == null ? type : valueType;
   }

   private boolean isJaxbClass(Class<?> classType)
   {
      if (classType.isAnnotationPresent(XmlRootElement.class) || classType.isAnnotationPresent(XmlType.class)
            || classType.isAnnotationPresent(XmlJavaTypeAdapter.class)
            || classType.isAnnotationPresent(XmlSeeAlso.class) || JAXBElement.class.equals(classType))
      {
         return true;
      }
      return false;

   }
   
   private boolean hasJacksonJaxbAnnotation(Annotation[] annotations)
   {
      for (Annotation ann : annotations)
      {
         String annotationName = ann.annotationType().getName();
         if (annotationName.contains("org.jboss.resteasy.annotations.providers.jackson")
               || annotationName.contains("com.fasterxml") || annotationName.contains("javax.xml.bind.annotation")
               || annotationName.contains("org.codehaus.jackson"))
         {
            return true;
         }
      }
      return false;
   }
   
   private Annotation[] getAllAnnotations(Class<?> classType)
   {
      Map<Class<?>, Annotation> annotations = new HashMap<Class<?>, Annotation>();
      for (Field field : classType.getDeclaredFields())
      {
         for (Annotation ann : field.getAnnotations())
         {
            annotations.put(ann.getClass(), ann);
         }
         for (Annotation ann : field.getDeclaringClass().getAnnotations())
         {
            annotations.put(ann.getClass(), ann);
         }
      }
      for (Constructor<?> constructor : classType.getDeclaredConstructors()) {
         for (Annotation ann : constructor.getAnnotations()) {
            annotations.put(ann.getClass(), ann);
         }
         for (Annotation ann : constructor.getDeclaringClass().getAnnotations())
         {
            annotations.put(ann.getClass(), ann);
         }
      }
      for (Method method : classType.getDeclaredMethods())
      {
         for (Annotation ann : method.getAnnotations())
         {
            annotations.put(ann.getClass(), ann);
         }
         
         for (Annotation ann : method.getDeclaringClass().getAnnotations())
         {
            annotations.put(ann.getClass(), ann);
         }
         
         for (Annotation[] annos : method.getParameterAnnotations())
         {
            for (Annotation ann : annos)
            {
               annotations.put(ann.getClass(), ann);
            }
         }

      }

      return annotations.values().toArray(new Annotation[annotations.size()]);

   }
}
