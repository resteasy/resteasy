package org.jboss.resteasy.links.impl;

import java.lang.reflect.Method;

import org.jboss.resteasy.links.i18n.Messages;
import org.jboss.resteasy.spi.Failure;

public class ServiceDiscoveryException extends Failure {

    public ServiceDiscoveryException(final Method m, final String s) {
        super(Messages.MESSAGES.discoveryFailedForMethod(m.getDeclaringClass().getName(), m.getName(), s));
    }

    public ServiceDiscoveryException(final Method m, final String s, final Throwable cause) {
        super(Messages.MESSAGES.discoveryFailedForMethod(m.getDeclaringClass().getName(), m.getName(), s), cause);
    }

}
