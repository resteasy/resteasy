package org.jboss.resteasy.core.interception;

import org.jboss.resteasy.core.ResourceMethod;
import org.jboss.resteasy.spi.ApplicationException;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface ResourceMethodContext
{
   HttpRequest getRequest();

   void setRequest(HttpRequest request);

   Object getTarget();

   void setTarget(Object target);

   ResourceMethod getMethod();

   Response proceed() throws Failure, WebApplicationException, ApplicationException;
}
