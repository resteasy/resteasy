package org.jboss.resteasy.core.interception;

import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;

import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface RequestContext
{
   HttpRequest getRequest();

   public void setRequest(HttpRequest request);

   HttpResponse getResponse();

   public void setResponse(HttpResponse response);

   void proceed();

   Map getContextData();

}
