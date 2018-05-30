package org.jboss.resteasy.core;

import org.jboss.resteasy.specimpl.BuiltResponse;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;

import java.lang.reflect.Method;
import java.util.concurrent.CompletionStage;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface ResourceInvoker
{
   CompletionStage<BuiltResponse> invoke(HttpRequest request, HttpResponse response);
   CompletionStage<BuiltResponse> invoke(HttpRequest request, HttpResponse response, Object target);

   Method getMethod();
}
