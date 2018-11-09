package org.jboss.resteasy.links.impl;

import javax.el.BeanELResolver;
import javax.el.ELContext;
import javax.el.ELResolver;
import java.beans.FeatureDescriptor;
import java.util.Iterator;

public class BaseELResolver extends ELResolver {

   private final ELResolver delegateResolver;
   private final Object base;

   public BaseELResolver(Object base) {
      this.base = base;
      this.delegateResolver = new BeanELResolver(true);
   }

   @Override
   public Class<?> getCommonPropertyType(ELContext context, Object base) {
      return null;
   }

   @Override
   public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context,
         Object base) {
      return null;
   }

   @Override
   public Class<?> getType(ELContext context, Object base, Object property) {
      return null;
   }

   @Override
   public Object getValue(ELContext context, Object base, Object property) {
      if (base == null){
         if("this".equals(property)){
            context.setPropertyResolved(true);
            return this.base;
         }
         return delegateResolver.getValue(context, this.base, property);
      }
      return null;
   }

   @Override
   public boolean isReadOnly(ELContext context, Object base, Object property) {
      return true;
   }

   @Override
   public void setValue(ELContext context, Object base, Object property,
         Object value) {
   }
}
