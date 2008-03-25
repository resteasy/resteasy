package org.resteasy.spi;

import org.resteasy.Failure;

import javax.ws.rs.core.Response;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface MethodInjector
{
   Response invoke(HttpRequest request, HttpResponse response, Object target) throws Failure;

   Object[] injectArguments(HttpRequest request, HttpResponse response) throws Failure;
}
