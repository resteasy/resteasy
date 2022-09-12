package org.jboss.resteasy.core;

import jakarta.ws.rs.ext.Providers;

/**
 *
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Feb 27, 2015
 */
public interface ProvidersContextRetainer
{
   void setProviders(Providers providers);
}
