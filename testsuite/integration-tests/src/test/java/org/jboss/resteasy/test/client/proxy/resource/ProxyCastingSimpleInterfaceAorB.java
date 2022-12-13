package org.jboss.resteasy.test.client.proxy.resource;

public interface ProxyCastingSimpleInterfaceAorB {
    <T> T as(Class<T> iface);
}
