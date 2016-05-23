package org.jboss.resteasy.spi.touri;

import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.specimpl.ResteasyUriBuilder;
import org.jboss.resteasy.util.AnnotationResolver;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractURITemplateAnnotationResolver implements
        URIResolver
{

   @SuppressWarnings("unchecked")
   public boolean handles(Class type)
   {
      return AnnotationResolver.getClassWithAnnotation(type, getAnnotationType()) != null;
   }

   @SuppressWarnings("unchecked")
   public String resolveURI(Object object)
   {
      Class<? extends Object> clazz = AnnotationResolver
              .getClassWithAnnotation(object.getClass(), getAnnotationType());
      ResteasyUriBuilder uriBuilderImpl = getUriBuilder(clazz);
      Map<String, PropertyDescriptor> descriptors = getPropertyDescriptors(clazz);
      List<Object> values = getValues(object, descriptors, uriBuilderImpl
              .getPathParamNamesInDeclarationOrder());
      return uriBuilderImpl.build(values.toArray()).toString();
   }

   protected abstract Class<? extends Annotation> getAnnotationType();

   protected abstract ResteasyUriBuilder getUriBuilder(Class<? extends Object> clazz);

   private List<Object> getValues(Object object,
                                  Map<String, PropertyDescriptor> descriptors, List<String> params)
   {
      List<Object> values = new ArrayList<Object>();
      for (String param : params)
      {
         PropertyDescriptor propertyDescriptor = descriptors.get(param);
         if (propertyDescriptor == null)
         {
            throw new RuntimeException(Messages.MESSAGES.couldNotFindGetterForParam(param));  
         }

         Method readMethod = propertyDescriptor.getReadMethod();
         if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers()))
         {
            readMethod.setAccessible(true);
         }

         try
         {
            values.add(readMethod.invoke(object, new Object[0]));
         }
         catch (Exception e)
         {
            throw new RuntimeException(Messages.MESSAGES.couldNotGetAValue(param), e);  
         }
      }
      return values;
   }

   private Map<String, PropertyDescriptor> getPropertyDescriptors(
           Class<? extends Object> clazz)
   {
      try
      {
         BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
         HashMap<String, PropertyDescriptor> results = new HashMap<String, PropertyDescriptor>();
         PropertyDescriptor[] propertyDescriptors = beanInfo
                 .getPropertyDescriptors();
         for (PropertyDescriptor propertyDescriptor : propertyDescriptors)
         {
            results.put(propertyDescriptor.getName(), propertyDescriptor);
         }
         return results;
      }
      catch (IntrospectionException e)
      {
         throw new RuntimeException(Messages.MESSAGES.couldNotIntrospectClass(clazz.getName()), e);  
      }
   }
}
