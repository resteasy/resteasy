package org.jboss.resteasy.test.nextgen.wadl.resources.locator;

import javax.ws.rs.Path;

/**
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */
@Path("/parent")
public class Parent {
    @Path("/child")
    public Child child() {
        return new Child();
    }
}
