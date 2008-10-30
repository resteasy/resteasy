package org.jboss.resteasy.plugins.spring;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.GetRestful;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

public class SpringBeanProcessorExt implements BeanFactoryPostProcessor {
	private Registry registry;
	private ResteasyProviderFactory factory;

	public SpringBeanProcessorExt(Registry registry,
			ResteasyProviderFactory factory) {
		this.registry = registry;
		this.factory = factory;
	}

	public void postProcessBeanFactory(
			final ConfigurableListableBeanFactory beanFactory)
			throws BeansException {
		final Collection<String> ignoreBeansList = createIgnoreList(beanFactory);
		beanFactory.addBeanPostProcessor(createBeanPostProcessor(beanFactory,
				ignoreBeansList));
	}

	@SuppressWarnings("unchecked")
	private Collection<String> createIgnoreList(
			final ConfigurableListableBeanFactory beanFactory) {
		Map<String, ResteasyRegistration> registries = beanFactory
				.getBeansOfType(ResteasyRegistration.class);

		final Collection<String> ignoreBeansList = new HashSet<String>();
		for (ResteasyRegistration registration : registries.values()) {
			ignoreBeansList.add(registration.getBeanName());
		}
		return ignoreBeansList;
	}

	protected BeanPostProcessor createBeanPostProcessor(
			final ConfigurableListableBeanFactory beanFactory,
			final Collection<String> ignoreBeansList) {
		return new BeanPostProcessor() {
			public Object postProcessAfterInitialization(Object bean,
					String name) throws BeansException {
				if (GetRestful.isRootResource(bean.getClass())) {
					if (!ignoreBeansList.contains(name)) {
						ResteasyRegistration registration = new ResteasyRegistration();
						registration.setBeanFactory(beanFactory);
						registration.setBeanName(name);
						registration.setRegistry(registry);
						registry.addResourceFactory(registration);
					}
				} else if (bean.getClass().isAnnotationPresent(Provider.class)) {
					factory.registerProviderInstance(bean);
				}
				return bean;
			}

			public Object postProcessBeforeInitialization(Object bean,
					String beanName) throws BeansException {
				return bean;
			}
		};
	}
}
