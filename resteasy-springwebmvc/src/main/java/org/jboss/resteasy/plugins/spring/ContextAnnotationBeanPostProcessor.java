package org.jboss.resteasy.plugins.spring;

import java.beans.PropertyDescriptor;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.jboss.resteasy.core.ContextParameterInjector;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.util.ReflectionUtils;

@SuppressWarnings("serial")
public class ContextAnnotationBeanPostProcessor extends
		InitDestroyAnnotationBeanPostProcessor implements
		InstantiationAwareBeanPostProcessor {

	private ResteasyProviderFactory factory;

	public ResteasyProviderFactory getFactory() {
		return factory;
	}

	public void setFactory(ResteasyProviderFactory factory) {
		this.factory = factory;
	}

	public boolean postProcessAfterInstantiation(Object bean, String beanName)
			throws BeansException {
		Class<? extends Object> clazz = bean.getClass();
		final InjectionMetadata newMetadata = new InjectionMetadata(clazz);
		ReflectionUtils.doWithFields(clazz,
				new ReflectionUtils.FieldCallback() {
					public void doWith(Field field) {
						if (field.isAnnotationPresent(Context.class)) {
							newMetadata.addInjectedField(new ContextElement(
									field, null));
						}
					}
				});
		try {
			newMetadata.injectFields(bean, beanName);
		} catch (Throwable ex) {
			throw new BeanCreationException(beanName,
					"Injection of resource fields failed", ex);
		}
		return true;
	}

	public PropertyValues postProcessPropertyValues(PropertyValues pvs,
			PropertyDescriptor[] pds, Object bean, String beanName)
			throws BeansException {
		Class<? extends Object> clazz = bean.getClass();
		final InjectionMetadata metadata = new InjectionMetadata(clazz);
		ReflectionUtils.doWithMethods(clazz,
				new ReflectionUtils.MethodCallback() {
					public void doWith(Method method) {
						if (method.isAnnotationPresent(Context.class)) {
							PropertyDescriptor pd = BeanUtils
									.findPropertyForMethod(method);
							metadata.addInjectedField(new ContextElement(
									method, pd));
						}
					}
				});
		try {
			metadata.injectMethods(bean, beanName, pvs);
		} catch (Throwable ex) {
			throw new BeanCreationException(beanName,
					"Injection of resource methods failed", ex);
		}
		return pvs;
	}

	@SuppressWarnings("unchecked")
	public Object postProcessBeforeInstantiation(Class beanClass,
			String beanName) throws BeansException {
		return null;
	}

	private class ContextElement extends InjectionMetadata.InjectedElement {

		protected boolean shareable = true;

		public ContextElement(Member member, PropertyDescriptor pd) {
			super(member, pd);
		}

		@Override
		protected Object getResourceToInject(Object target,
				String requestingBeanName) {
			return new ContextParameterInjector(this.getResourceType(), factory)
					.inject();
		}
	}

}
