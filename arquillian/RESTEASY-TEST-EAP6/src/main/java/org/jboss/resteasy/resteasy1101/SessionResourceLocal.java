package org.jboss.resteasy.resteasy1101;

import javax.ejb.Local;
import javax.ws.rs.Path;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Oct 6, 2014
 */
@Local
@Path("test")
public interface SessionResourceLocal extends SessionResourceParent
{
}