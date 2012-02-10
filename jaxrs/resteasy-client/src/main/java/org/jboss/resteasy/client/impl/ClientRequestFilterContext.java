package org.jboss.resteasy.client.impl;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.specimpl.ResponseBuilderImpl;

import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.FilterContext;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ClientRequestFilterContext implements FilterContext
{
   protected ClientInvocation request;
   protected ClientResponse res;

   @Override
   public Map<String, Object> getProperties()
   {
      return request.getProperties();
   }

   @Override
   public Request getRequest()
   {
      return request;
   }

   @Override
   public Response getResponse()
   {
      return res;
   }

   @Override
   public void setRequest(Request req)
   {
      if (!(req instanceof ClientInvocation))
      {
         throw new IllegalArgumentException("Non-Resteasy Request object");
      }
      request = (ClientInvocation)req;
   }

   @Override
   public void setResponse(Response res)
   {
      if (!(res instanceof ClientResponse))
      {
         throw new IllegalArgumentException("Non-Resteasy Response object");
      }
      this.res = (ClientResponse)res;
   }

   @Override
   public Request.RequestBuilder getRequestBuilder()
   {
      return new ClientRequestBuilder(request);
   }

   @Override
   public Response.ResponseBuilder getResponseBuilder()
   {
      return new ResponseBuilderImpl();
   }

   @Override
   public Response.ResponseBuilder createResponse()
   {
      return null;
   }
}
