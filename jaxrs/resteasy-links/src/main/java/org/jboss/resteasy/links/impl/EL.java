package org.jboss.resteasy.links.impl;

import org.jboss.el.ExpressionFactoryImpl;
import org.jboss.el.lang.FunctionMapperImpl;
import org.jboss.el.lang.VariableMapperImpl;

import javax.el.ArrayELResolver;
import javax.el.BeanELResolver;
import javax.el.CompositeELResolver;
import javax.el.ELContext;
import javax.el.ELResolver;
import javax.el.ExpressionFactory;
import javax.el.FunctionMapper;
import javax.el.ListELResolver;
import javax.el.MapELResolver;
import javax.el.ResourceBundleELResolver;
import javax.el.VariableMapper;

public class EL {
	public static final ExpressionFactory EXPRESSION_FACTORY = new ExpressionFactoryImpl();

	private static ELResolver createELResolver(Object base) {
		CompositeELResolver resolver = new CompositeELResolver();
		if(base != null)
			resolver.add(new BaseELResolver(base));
		resolver.add(new MapELResolver());
		resolver.add(new ListELResolver());
		resolver.add(new ArrayELResolver());
		resolver.add(new ResourceBundleELResolver());
		resolver.add(new BeanELResolver());
		return resolver;
	}

	public static ELContext createELContext(Object base) {
		return createELContext(createELResolver(base), new FunctionMapperImpl());
	}

	public static ELContext createELContext(final ELResolver resolver,
			final FunctionMapper functionMapper) {
		return new ELContext() {
			final VariableMapperImpl variableMapper = new VariableMapperImpl();

			@Override
			public ELResolver getELResolver() {
				return resolver;
			}

			@Override
			public FunctionMapper getFunctionMapper() {
				return functionMapper;
			}

			@Override
			public VariableMapper getVariableMapper() {
				return variableMapper;
			}

		};
	}
}
