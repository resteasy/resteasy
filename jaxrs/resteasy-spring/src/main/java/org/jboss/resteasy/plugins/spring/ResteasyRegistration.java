package org.jboss.resteasy.plugins.spring;

import org.jboss.resteasy.spi.Registry;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;

public class ResteasyRegistration implements InitializingBean, BeanFactoryAware {

	private Registry registry;

	private String context = "";

	private String beanName;

	private BeanFactory beanFactory;

	
	public ResteasyRegistration() {
		super();
	}

	public ResteasyRegistration(Registry registry, String context,
			String beanName, BeanFactory beanFactory) {
		super();
		this.registry = registry;
		this.context = context;
		this.beanName = beanName;
		this.beanFactory = beanFactory;
	}

	public Registry getRegistry() {
		return registry;
	}

	public void setRegistry(Registry registry) {
		this.registry = registry;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public void afterPropertiesSet() throws Exception {
		SpringResourceFactory resourceFactory = new SpringResourceFactory(
				beanName);
		resourceFactory.setBeanFactory(beanFactory);
		if (StringUtils.hasText(context))
			registry.addResourceFactory(resourceFactory, context);
		else
			registry.addResourceFactory(resourceFactory);
	}

	@Required
	public String getBeanName() {
		return this.beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}
}
