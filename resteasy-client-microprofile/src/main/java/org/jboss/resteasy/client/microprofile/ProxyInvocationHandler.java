package org.jboss.resteasy.client.microprofile;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Set;

import javax.ws.rs.client.ResponseProcessingException;
import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;

class ProxyInvocationHandler implements InvocationHandler
{

   private Object target;

   private Set<Object> providerInstances;

   ProxyInvocationHandler(Object target, Set<Object> providerInstances)
   {
      this.target = target;
      this.providerInstances = providerInstances;
   }

   @SuppressWarnings("unchecked")
   @Override
   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
   {
      try
      {

         boolean replacementNeeded = false;
         Object[] argsReplacement = args != null ? new Object[args.length] : null;
         Annotation[][] parameterAnnotations = method.getParameterAnnotations();

         for (Object p : providerInstances)
         {
            if (p instanceof ParamConverterProvider)
            {

               int index = 0;
               for (Object arg : args)
               {

                  if (parameterAnnotations[index].length > 0)
                  { // does a parameter converter apply?

                     ParamConverter<?> converter = ((ParamConverterProvider) p).getConverter(arg.getClass(), null,
                           parameterAnnotations[index]);
                     if (converter != null)
                     {
                        Type[] genericTypes = getGenericTypes(converter.getClass());
                        if (genericTypes.length == 1)
                        {

                           // minimum supported types
                           switch (genericTypes[0].getTypeName())
                           {
                              case "java.lang.String" :
                                 ParamConverter<String> stringConverter = (ParamConverter<String>) converter;
                                 argsReplacement[index] = stringConverter.toString((String) arg);
                                 replacementNeeded = true;
                                 break;
                              case "java.lang.Integer" :
                                 ParamConverter<Integer> intConverter = (ParamConverter<Integer>) converter;
                                 argsReplacement[index] = intConverter.toString((Integer) arg);
                                 replacementNeeded = true;
                                 break;
                              case "java.lang.Boolean" :
                                 ParamConverter<Boolean> boolConverter = (ParamConverter<Boolean>) converter;
                                 argsReplacement[index] = boolConverter.toString((Boolean) arg);
                                 replacementNeeded = true;
                                 break;
                              default :
                                 continue;
                           }
                        }
                     }
                  }
                  else
                  {
                     argsReplacement[index] = arg;
                  }
                  index++;
               }
            }
         }

         return replacementNeeded ? method.invoke(target, argsReplacement) : method.invoke(target, args);

      }
      catch (InvocationTargetException e)
      {

         if (e.getCause() instanceof ResponseProcessingException)
         {
            ResponseProcessingException rpe = (ResponseProcessingException) e.getCause();
            Throwable cause = rpe.getCause();
            if (cause instanceof RuntimeException)
            {
               throw cause;
            }
         }

         throw e;
      }
   }

   private Type[] getGenericTypes(Class aClass)
   {
      Type[] genericInterfaces = aClass.getGenericInterfaces();
      Type[] genericTypes = new Type[]
      {};
      for (Type genericInterface : genericInterfaces)
      {
         if (genericInterface instanceof ParameterizedType)
         {
            genericTypes = ((ParameterizedType) genericInterface).getActualTypeArguments();
         }
      }
      return genericTypes;
   }
}
