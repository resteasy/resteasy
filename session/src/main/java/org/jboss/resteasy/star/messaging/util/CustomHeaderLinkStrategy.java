package org.jboss.resteasy.star.messaging.util;

import javax.ws.rs.core.Response;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class CustomHeaderLinkStrategy implements LinkStrategy
{
   @Override
   public void setLinkHeader(Response.ResponseBuilder builder, String title, String rel, String href, String type)
   {
      String headerName = null;
      if (title != null)
      {
         headerName = title;
      }
      else if (rel != null)
      {
         headerName = rel;
      }
      else
      {
         throw new RuntimeException("Cannot figure out header name");
      }
      headerName = "msg-" + headerName;
      builder.header(headerName, href);
      if (type != null)
      {
         builder.header(headerName + "-type", type);
      }
   }
}
