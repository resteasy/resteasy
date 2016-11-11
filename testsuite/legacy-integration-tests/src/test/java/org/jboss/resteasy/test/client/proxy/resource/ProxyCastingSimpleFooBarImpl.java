package org.jboss.resteasy.test.client.proxy.resource;

import javax.ws.rs.Path;

@Path("/foobar")
public class ProxyCastingSimpleFooBarImpl implements ProxyCastingSimpleFooBar {
    @Override
    public ProxyCastingSimpleInterfaceAorB getThing(String thing) {
        if ("a".equalsIgnoreCase(thing)) {
            return new ProxyCastingSimpleInterfaceA() {
                @Override
                public String getFoo() {
                    return "FOO";
                }

                @Override
                public <T> T as(Class<T> iface) {
                    return iface.cast(this);
                }
            };
        } else if ("b".equalsIgnoreCase(thing)) {
            return new ProxyCastingSimpleInterfaceB() {
                @Override
                public String getBar() {
                    return "BAR";
                }

                @Override
                public <T> T as(Class<T> iface) {
                    return iface.cast(this);
                }
            };
        } else {
            throw new IllegalArgumentException("Bad arg: " + thing);
        }
    }
}
