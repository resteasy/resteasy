package org.jboss.resteasy.test.providers.jaxb.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/zoo")
public class InheritanceResource {

    @GET
    @Produces("application/xml")
    public InheritanceZoo getZoo() {
        InheritanceZoo aZoo = new InheritanceZoo();
        aZoo.add(new InheritanceDog("Foo"));
        aZoo.add(new InheritanceCat("Bar"));
        return aZoo;
    }
}
