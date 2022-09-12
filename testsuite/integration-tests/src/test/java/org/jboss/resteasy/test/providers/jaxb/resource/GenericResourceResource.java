package org.jboss.resteasy.test.providers.jaxb.resource;

import jakarta.ejb.Stateless;
import jakarta.ws.rs.Path;

@Stateless
@Path("/test")
public class GenericResourceResource extends GenericResourceOtherAbstractResource<GenericResourceModel> {

}
