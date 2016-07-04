package org.jboss.resteasy.test.nextgen.wadl.resources.locator;

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
