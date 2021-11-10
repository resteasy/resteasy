package org.jboss.resteasy.links.impl;


import jakarta.el.ArrayELResolver;
import jakarta.el.BeanELResolver;
import jakarta.el.CompositeELResolver;
import jakarta.el.ELContext;
import jakarta.el.ELResolver;
import jakarta.el.ExpressionFactory;
import jakarta.el.ListELResolver;
import jakarta.el.MapELResolver;
import jakarta.el.ResourceBundleELResolver;
import jakarta.el.StandardELContext;

public class EL {
   public static final ExpressionFactory EXPRESSION_FACTORY = ExpressionFactory.newInstance();

   private static ELResolver createELResolver(Object base) {
      CompositeELResolver resolver = new CompositeELResolver();
      if (base != null) { resolver.add(new BaseELResolver(base)); }
      resolver.add(new MapELResolver());
      resolver.add(new ListELResolver());
      resolver.add(new ArrayELResolver());
      resolver.add(new ResourceBundleELResolver());
      resolver.add(new BeanELResolver());
      return resolver;
   }

   public static ELContext createELContext(final Object base) {
      return new StandardELContext(EXPRESSION_FACTORY) {
         @Override
         public ELResolver getELResolver() {
            return createELResolver(base);
         }

      };
   }
}
