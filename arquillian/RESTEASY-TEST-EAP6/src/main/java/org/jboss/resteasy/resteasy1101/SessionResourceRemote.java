package org.jboss.resteasy.resteasy1101;

import javax.ejb.Remote;
import javax.ws.rs.Path;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Oct 6, 2014
 */
@Remote
@Path("test")
public interface SessionResourceRemote extends SessionResourceParent
{
}