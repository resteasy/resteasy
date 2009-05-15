package org.jboss.resteasy.spi.touri;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.resteasy.specimpl.UriBuilderImpl;
import org.jboss.resteasy.util.AnnotationResolver;

public class URITemplateAnnotationResolver implements URIResolver
{

   @Override
   public boolean handles(Class type)
   {
      return AnnotationResolver.getClassWithAnnotation(type, URITemplate.class) != null;
   }

   @SuppressWarnings("unchecked")
   public String resolveURI(Object object)
   {
      Class<? extends Object> clazz = AnnotationResolver
            .getClassWithAnnotation(object.getClass(), URITemplate.class);
      UriBuilderImpl uriBuilderImpl = getUriBuilder(clazz);
      Map<String, PropertyDescriptor> descriptors = getPropertyDescriptors(clazz);
      List<Object> values = getValues(object, descriptors, uriBuilderImpl
            .getPathParamNamesInDeclarationOrder());
      return uriBuilderImpl.build(values.toArray()).toString();
   }

   private UriBuilderImpl getUriBuilder(Class<? extends Object> clazz)
   {
      String uriTemplate = clazz.getAnnotation(URITemplate.class).value();
      UriBuilderImpl uriBuilderImpl = new UriBuilderImpl();
      uriBuilderImpl.replacePath(uriTemplate);
      return uriBuilderImpl;
   }

   private List<Object> getValues(Object object,
         Map<String, PropertyDescriptor> descriptors, List<String> params)
   {
      List<Object> values = new ArrayList<Object>();
      for (String param : params)
      {
         PropertyDescriptor propertyDescriptor = descriptors.get(param);
         if (propertyDescriptor == null)
         {
            throw new RuntimeException(
                  "URITemplateAnnotationResolver could not find a getter for param "
                        + param);
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
            throw new RuntimeException(
                  "URITemplateAnnotationResolver could not get a value for "
                        + param, e);
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
         throw new RuntimeException(
               "URITemplateAnnotationResolver could not introspect class "
                     + clazz.getName(), e);
      }
   }
}
