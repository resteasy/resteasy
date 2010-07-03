package org.jboss.resteasy.star.messaging.integration;

import javax.servlet.FilterConfig;
import javax.servlet.ServletConfig;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class RestMessagingJaxrsBootstrap extends Application
{
   @Context
   private ServletConfig servletConfig;

   @Context
   private FilterConfig filterConfig;


}
