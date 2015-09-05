package org.jboss.resteasy.wadl.testing.locator;

import javax.ws.rs.GET;

/**
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */
public class Child {

    @GET
    public String get() {
        return "child";
    }
}
