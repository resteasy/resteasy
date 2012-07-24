package org.jboss.resteasy.links.impl;

import org.jboss.resteasy.spi.Failure;

import java.lang.reflect.Method;

public class ServiceDiscoveryException extends Failure {

	public ServiceDiscoveryException(Method m, String s) {
		super("Discovery failed for method "+m.getDeclaringClass().getName()+"."+m.getName()+": "+s);
	}

	public ServiceDiscoveryException(Method m, String s, Throwable cause) {
		super("Discovery failed for method "+m.getDeclaringClass().getName()+"."+m.getName()+": "+s, cause);
	}

}
