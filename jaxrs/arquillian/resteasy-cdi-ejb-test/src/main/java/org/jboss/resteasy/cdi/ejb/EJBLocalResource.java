package org.jboss.resteasy.cdi.ejb;

import javax.ejb.Local;
import javax.ws.rs.Path;

/**
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Jun 29, 2012
 */
@Local
@Path("/")
public interface EJBLocalResource extends EJBResourceParent
{
}

