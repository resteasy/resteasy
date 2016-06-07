package org.jboss.resteasy.plugins.providers.jaxb;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.jboss.resteasy.util.FindAnnotation;
import org.jboss.resteasy.util.Types;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Apr 8, 2015
 */
class XmlAdapterWrapper extends XmlAdapter<Object, Object>
{
   private XmlAdapter<Object, Object> delegate;
   private Class<?> clazz;
   
   public XmlAdapterWrapper(XmlAdapter<Object, Object> delegate, Class<?> clazz)
   {
      this.delegate = delegate;
      this.clazz = clazz;
   }
   
   @Override
   public Object unmarshal(Object v) throws Exception
   {
      return delegate.unmarshal(v);
   }
   
   @Override
   public Object marshal(Object v) throws Exception
   {
      return delegate.marshal(v);
   }
   
   public Class<?> getValueType()
   {
      return clazz;
   }
   
   protected static Class<?> xmlAdapterValueType(Class<?> baseType, Annotation[] annotations)
   {
      Class<?> clazz = baseType;
      if (baseType.isAnnotationPresent(XmlJavaTypeAdapter.class))
      {
         XmlJavaTypeAdapter xmlJavaTypeAdapter = FindAnnotation.findAnnotation(baseType, annotations, XmlJavaTypeAdapter.class);
         Class<? extends XmlAdapter> adapterClass = xmlJavaTypeAdapter.value();
         ParameterizedType xmlAdapterType = ParameterizedType.class.cast(adapterClass.getGenericSuperclass());
         Class<?> valueType = Types.getArgumentType(xmlAdapterType, 0);
         Class<?> boundType = Types.getArgumentType(xmlAdapterType, 1);
         if (boundType.isAssignableFrom(baseType))
         {
            clazz = valueType;
         }
      }
      return clazz;
   }
   
   @SuppressWarnings("unchecked")
   protected static XmlAdapterWrapper getXmlAdapter(Class<?> baseType, Annotation[] annotations)
   {
      if (baseType.isAnnotationPresent(XmlJavaTypeAdapter.class))
      {
         XmlJavaTypeAdapter xmlJavaTypeAdapter = FindAnnotation.findAnnotation(baseType, annotations, XmlJavaTypeAdapter.class);
         Class<? extends XmlAdapter> adapterClass = xmlJavaTypeAdapter.value();
         ParameterizedType xmlAdapterType = ParameterizedType.class.cast(adapterClass.getGenericSuperclass());
         Class<?> boundType = Types.getArgumentType(xmlAdapterType, 1);
         if (boundType.isAssignableFrom(baseType))
         {
            try
            {
               Class<?> valueType = Types.getArgumentType(xmlAdapterType, 0);
               return new XmlAdapterWrapper(adapterClass.newInstance(), valueType);
            }
            catch (Exception e)
            {
               throw new JAXBMarshalException(e);
            }
         }
      }
      return null;
   }
}
