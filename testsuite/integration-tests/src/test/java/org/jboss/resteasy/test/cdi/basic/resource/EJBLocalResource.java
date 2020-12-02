package org.jboss.resteasy.test.cdi.basic.resource;

import javax.ejb.Local;
import jakarta.ws.rs.Path;

@Local
@Path("/")
public interface EJBLocalResource extends EJBResourceParent {
}
