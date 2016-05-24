package org.jboss.resteasy.tests.typevar.sample;

import javax.ws.rs.Path;

@Path(value = "/say")
public interface HelloString extends Hello<String> {

}
