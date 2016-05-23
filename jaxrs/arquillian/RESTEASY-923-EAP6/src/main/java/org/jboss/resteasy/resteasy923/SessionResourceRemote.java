package org.jboss.resteasy.resteasy923;

import javax.ejb.Remote;
import javax.ws.rs.Path;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Mar 26, 2014
 */
@Remote
@Path("test")
public interface SessionResourceRemote extends SessionResourceParent
{
}