package org.jboss.resteasy.examples;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

/**
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */
public class OrdersApplication extends Application {
	HashSet<Object> singletons = new HashSet<Object>();

	public OrdersApplication() {
		singletons.add(new Orders());
	}

	@Override
	public Set<Class<?>> getClasses() {
		HashSet<Class<?>> set = new HashSet<Class<?>>();
		return set;
	}

	@Override
	public Set<Object> getSingletons() {
		return singletons;
	}

}
