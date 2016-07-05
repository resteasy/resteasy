package org.jboss.resteasy.test.validation.cdi.resource;

import javax.ejb.Local;
import javax.ws.rs.Path;

@Local
@Path("test")
public interface SessionResourceLocal extends SessionResourceParent {
}