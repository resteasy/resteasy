package org.jboss.resteasy.client.jaxrs.internal.proxy.processors;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Stack;

import javax.ws.rs.BeanParam;
import javax.ws.rs.CookieParam;
import javax.ws.rs.Encoded;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.Form;
import org.jboss.resteasy.annotations.ClientURI;
import org.jboss.resteasy.client.jaxrs.i18n.Messages;
import org.jboss.resteasy.client.jaxrs.internal.ClientConfiguration;
import org.jboss.resteasy.client.jaxrs.internal.proxy.processors.invocation.CookieParamProcessor;
import org.jboss.resteasy.client.jaxrs.internal.proxy.processors.invocation.FormParamProcessor;
import org.jboss.resteasy.client.jaxrs.internal.proxy.processors.invocation.HeaderParamProcessor;
import org.jboss.resteasy.client.jaxrs.internal.proxy.processors.invocation.MessageBodyParameterProcessor;
import org.jboss.resteasy.client.jaxrs.internal.proxy.processors.invocation.URIParamProcessor;
import org.jboss.resteasy.client.jaxrs.internal.proxy.processors.webtarget.MatrixParamProcessor;
import org.jboss.resteasy.client.jaxrs.internal.proxy.processors.webtarget.PathParamProcessor;
import org.jboss.resteasy.client.jaxrs.internal.proxy.processors.webtarget.QueryParamProcessor;
import org.jboss.resteasy.util.FindAnnotation;
import org.jboss.resteasy.util.MediaTypeHelper;

public class ProcessorFactory
{

	public static Object[] createProcessors(Class declaringClass, Method method, ClientConfiguration configuration)
	{
		return createProcessors(declaringClass, method, configuration, null);
	}
	
	public static Object[] createProcessors(Class declaringClass, Method method, ClientConfiguration configuration, MediaType defaultConsumes)
	{
      Object[] params = new Object[method.getParameterTypes().length];
      for (int i = 0; i < method.getParameterTypes().length; i++)
      {
         Class<?> type = method.getParameterTypes()[i];
         Annotation[] annotations = method.getParameterAnnotations()[i];
         Type genericType = method.getGenericParameterTypes()[i];
         if (TypeVariable.class.isInstance(genericType) && declaringClass.isInterface() && !declaringClass.equals(method.getDeclaringClass())) {
        	 genericType = getTypeArgument((TypeVariable)genericType, declaringClass, method.getDeclaringClass());
         }
         AccessibleObject target = method;
         params[i] = ProcessorFactory.createProcessor(declaringClass, configuration, type, annotations, genericType, target, defaultConsumes, false);
      }
      return params;
   }

	   public static Object createProcessor(Class<?> declaring,
                                               ClientConfiguration configuration, Class<?> type,
                                               Annotation[] annotations, Type genericType, AccessibleObject target,
                                               boolean ignoreBody)
	   {
		   return createProcessor(declaring, configuration, type, annotations, genericType, target, null, ignoreBody);
	   }
	   
	   public static Object createProcessor(Class<?> declaring,
                                               ClientConfiguration configuration, Class<?> type,
                                               Annotation[] annotations, Type genericType, AccessibleObject target, MediaType defaultConsumes,
                                               boolean ignoreBody)
   {
      Object processor = null;

      QueryParam query;
      HeaderParam header;
      MatrixParam matrix;
      PathParam uriParam;
      CookieParam cookie;
      FormParam formParam;
      // Form form;

      boolean isEncoded = FindAnnotation.findAnnotation(annotations,
              Encoded.class) != null;

      if ((query = FindAnnotation.findAnnotation(annotations, QueryParam.class)) != null)
      {
         processor = new QueryParamProcessor(query.value());
      }
      else if ((header = FindAnnotation.findAnnotation(annotations,
              HeaderParam.class)) != null)
      {
         processor = new HeaderParamProcessor(header.value());
      }
      else if ((cookie = FindAnnotation.findAnnotation(annotations,
              CookieParam.class)) != null)
      {
         processor = new CookieParamProcessor(cookie.value());
      }
      else if ((uriParam = FindAnnotation.findAnnotation(annotations,
              PathParam.class)) != null)
      {
         processor = new PathParamProcessor(uriParam.value(), isEncoded);
      }
      else if ((matrix = FindAnnotation.findAnnotation(annotations,
              MatrixParam.class)) != null)
      {
         processor = new MatrixParamProcessor(matrix.value());
      }
      else if ((formParam = FindAnnotation.findAnnotation(annotations,
              FormParam.class)) != null)
      {
         processor = new FormParamProcessor(formParam.value());
      }
      else if ((/* form = */FindAnnotation.findAnnotation(annotations,
              Form.class)) != null)
      {
         processor = new FormProcessor(type, configuration);
      }
      else if ((/* form = */FindAnnotation.findAnnotation(annotations,
              BeanParam.class)) != null)
      {
         processor = new FormProcessor(type, configuration);
      }
      else if ((FindAnnotation.findAnnotation(annotations,
              Context.class)) != null)
      {
         processor = null;
      }
      else if (type.equals(Cookie.class))
      {
         processor = new CookieParamProcessor(null);
      }
      // this is for HATEAOS clients
      else if (FindAnnotation.findAnnotation(annotations, ClientURI.class.getName()) != null)
      {
         processor = new URIParamProcessor();
      }
      else if (!ignoreBody)
      {
         MediaType mediaType = MediaTypeHelper.getConsumes(declaring, target);
         if(mediaType == null)
        	 mediaType = defaultConsumes;
         if (mediaType == null)
         {
            throw new RuntimeException(Messages.MESSAGES.mustDefineConsumesType());
         }
         processor = new MessageBodyParameterProcessor(mediaType, type,
                 genericType, annotations);
      }
      return processor;
   }
   
   static Type getTypeArgument(TypeVariable<?> var, Class<?> clazz, Class<?> baseInterface) {
      TypeVariable<?> tv = var;
      // collect superinterfaces
      Stack<Type> superinterfaces = new Stack<Type>();
      Type currentType;
      Class<?> currentClass = clazz;
      recursivePush(currentClass, baseInterface, superinterfaces); 

      while (!superinterfaces.isEmpty()) {
         currentType = superinterfaces.pop();

         if (currentType instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) currentType;
            Class<?> rawType = (Class) pt.getRawType();
            int argIndex = Arrays.asList(rawType.getTypeParameters()).indexOf(tv);
            if (argIndex > -1) {
               Type typeArg = pt.getActualTypeArguments()[argIndex];
               if (typeArg instanceof TypeVariable) {
                  // type argument is another type variable - look for the value of that
                  // variable in subclasses
                  tv = (TypeVariable<?>) typeArg;
                  continue;
               } else {
                  // found the value - return it
                  return typeArg;
               }
            }
         }

         // needed type argument not supplied - break and throw exception
         break;
      }
      throw new IllegalArgumentException(Messages.MESSAGES.doesNotSpecifyTypeParameter(var));
   }

   static void recursivePush(Type t, Class<?> baseInterface, Stack<Type> superinterfaces) {
      Class<?> currentClass = null;
      if (t instanceof Class) {
         currentClass = (Class) t;
      } else if (t instanceof ParameterizedType) {
         currentClass = (Class) ((ParameterizedType) t).getRawType();
      }
      if (baseInterface.isAssignableFrom(currentClass)) {
         superinterfaces.push(t);

         for (Type otherType : currentClass.getGenericInterfaces()) {
            recursivePush(otherType, baseInterface, superinterfaces);
         }
      }

   }
}
