package org.jboss.resteasy.resteasy1008;

import javax.ejb.Stateful;
import javax.validation.constraints.Min;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Mar 26, 2014
 */
@Path("test")
@Stateful
public class SessionResourceImpl implements SessionResource
{
   private static final Logger log = LoggerFactory.getLogger(SessionResourceImpl.class);

   @GET
   @Path("resource/{param}")
   @Produces(MediaType.TEXT_PLAIN)
   public int test(@PathParam("param") int param)
   {
      log.info("entering SessionResourceImpl.test()");
      return param;
   }
}
