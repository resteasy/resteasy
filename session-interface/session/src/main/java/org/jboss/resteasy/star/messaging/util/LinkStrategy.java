package org.jboss.resteasy.star.messaging.util;

import javax.ws.rs.core.Response;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface LinkStrategy
{
   public void setLinkHeader(Response.ResponseBuilder builder, String title, String rel, String href, String type);
}