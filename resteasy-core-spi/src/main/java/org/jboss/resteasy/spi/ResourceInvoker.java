package org.jboss.resteasy.spi;

import java.lang.reflect.Method;
import java.util.concurrent.CompletionStage;

import javax.ws.rs.core.Response;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface ResourceInvoker
{
   CompletionStage<? extends Response> invoke(HttpRequest request, HttpResponse response);

   CompletionStage<? extends Response> invoke(HttpRequest request, HttpResponse response, Object target);

   Method getMethod();
}
