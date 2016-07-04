package org.jboss.resteasy.links.impl;


import javax.el.ArrayELResolver;
import javax.el.BeanELResolver;
import javax.el.CompositeELResolver;
import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.el.ListELResolver;
import javax.el.MapELResolver;
import javax.el.ResourceBundleELResolver;
import javax.el.StandardELContext;

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
