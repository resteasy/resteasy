package org.jboss.resteasy.test.validation.cdi.resource;

import jakarta.ejb.Local;
import jakarta.ws.rs.Path;

@Local
@Path("test")
public interface SessionResourceLocal extends SessionResourceParent {
}
