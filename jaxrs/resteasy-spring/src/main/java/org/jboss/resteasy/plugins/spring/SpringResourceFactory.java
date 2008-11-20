package org.jboss.resteasy.plugins.spring;

import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.InjectorFactory;
import org.jboss.resteasy.spi.ResourceFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
* 
* 
* @author <a href="mailto:sduskis@gmail.com">Solomn Duskis</a>
* @version $Revision: 1 $
*/

public class SpringResourceFactory implements ResourceFactory, BeanFactoryAware {

	private BeanFactory beanFactory;
	private String beanName;
	private Class<?> scannableClass;

	public SpringResourceFactory(String beanName) {
		this.beanName = beanName;
	}

	public Object createResource(HttpRequest request, HttpResponse response,
			InjectorFactory factory) {
		return beanFactory.getBean(beanName);
	}

	public Class<?> getScannableClass() {
		return scannableClass != null ? scannableClass : beanFactory.getType(this.beanName);
	}

	public void registered(InjectorFactory factory) {
		// do nothing.  Rely on Spring. 
	}

	public void requestFinished(HttpRequest request, HttpResponse response,
			Object resource) {
		// do nothing
	}

	public void unregistered() {
		// do nothing
	}

	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

   public void setScannableClass(Class<?> scannableClass)
   {
      this.scannableClass = scannableClass;
   }

}
