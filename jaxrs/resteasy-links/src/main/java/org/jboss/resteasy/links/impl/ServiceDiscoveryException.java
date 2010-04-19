package org.jboss.resteasy.links.impl;

import java.lang.reflect.Method;

import org.jboss.resteasy.spi.Failure;

public class ServiceDiscoveryException extends Failure {

	public ServiceDiscoveryException(Method m, String s) {
		super("Discovery failed for method "+m.getDeclaringClass().getName()+"."+m.getName()+": "+s);
	}

}
