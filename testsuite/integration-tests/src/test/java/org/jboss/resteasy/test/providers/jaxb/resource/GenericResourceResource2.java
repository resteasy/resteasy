package org.jboss.resteasy.test.providers.jaxb.resource;

import jakarta.ejb.Stateless;
import jakarta.ws.rs.Path;

@Path("test2")
@Stateless
public class GenericResourceResource2 extends GenericResourceAbstractResource<GenericResourceModel> {

}
