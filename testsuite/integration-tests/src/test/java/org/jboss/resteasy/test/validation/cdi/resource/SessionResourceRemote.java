package org.jboss.resteasy.test.validation.cdi.resource;

import jakarta.ejb.Remote;
import jakarta.ws.rs.Path;

@Remote
@Path("test")
public interface SessionResourceRemote extends SessionResourceParent {
}
