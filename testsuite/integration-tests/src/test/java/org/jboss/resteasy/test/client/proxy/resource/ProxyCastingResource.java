package org.jboss.resteasy.test.client.proxy.resource;

import javax.ws.rs.Path;

@Path("/foobar")
public class ProxyCastingResource implements ProxyCastingInterfaceA, ProxyCastingInterfaceB, ProxyCastingNothing {
    @Override
    public String getFoo() {
        return "FOO";
    }

    @Override
    public String getBar() {
        return "BAR";
    }
}
