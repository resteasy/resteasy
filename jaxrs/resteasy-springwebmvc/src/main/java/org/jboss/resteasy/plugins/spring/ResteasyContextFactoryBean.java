package org.jboss.resteasy.plugins.spring;

import org.jboss.resteasy.core.ContextParameterInjector;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Required;

public class ResteasyContextFactoryBean implements FactoryBean {

	private Class<?> objectType;
	private ResteasyProviderFactory providerFactory;

	@Required
	public ResteasyProviderFactory getProviderFactory() {
		return providerFactory;
	}

	public void setProviderFactory(ResteasyProviderFactory providerFactory) {
		this.providerFactory = providerFactory;
	}

	public void setObjectType(Class<?> objectType) {
		this.objectType = objectType;
	}

	public Object getObject() throws Exception {
		return new ContextParameterInjector(objectType, providerFactory).inject();
	}

	@Required
	public Class<?> getObjectType() {
		return objectType;
	}

	public boolean isSingleton() {
		return true;
	}

}
