package org.jboss.resteasy.plugins.spring;

import java.util.List;

import org.jboss.resteasy.core.ContextParameterInjector;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.Ordered;

/**
* 
* @author <a href="mailto:sduskis@gmail.com">Solomn Duskis</a>
* @version $Revision: 1 $
*/

public class ResteasyContextFactoryBean implements BeanFactoryPostProcessor,
		Ordered {

	private List<Class<?>> objectTypes;
	
	private int order = 0;

	@Required
	public List<Class<?>> getObjectTypes() {
		return objectTypes;
	}

	public void setObjectTypes(List<Class<?>> objectTypes) {
		this.objectTypes = objectTypes;
	}

	public void postProcessBeanFactory(
			ConfigurableListableBeanFactory beanFactory) throws BeansException {
		for (final Class<?> clazz : objectTypes) {
			ObjectFactory of = new ObjectFactory() {
				public Object getObject() throws BeansException {
					// TODO: is this right? Can't we inject this? Perhaps this
					// can be handled with Spring's web based infrastructure?
					// Ideally, all @Context injections, including Resource
					// method injections.  For now, this seems like it should work
					return new ContextParameterInjector(clazz,
							ResteasyProviderFactory.getInstance()).inject();
				}
			};
			try {
				beanFactory.registerResolvableDependency(clazz, of);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}
}
