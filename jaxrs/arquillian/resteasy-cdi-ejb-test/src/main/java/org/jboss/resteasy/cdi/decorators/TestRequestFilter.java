package org.jboss.resteasy.cdi.decorators;

import java.io.IOException;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Nov 14, 2012
 */
@Provider
@FilterBinding
public class TestRequestFilter implements ContainerRequestFilter
{
   @Inject private Logger log;
   
   @Override
   public void filter(ContainerRequestContext requestContext) throws IOException
   {
      log.info("executing TestRequestFilter.filter()");
   }
}
