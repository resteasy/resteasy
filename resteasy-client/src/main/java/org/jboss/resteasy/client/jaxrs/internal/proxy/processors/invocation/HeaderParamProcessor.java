package org.jboss.resteasy.client.jaxrs.internal.proxy.processors.invocation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import org.jboss.resteasy.client.jaxrs.internal.ClientConfiguration;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class HeaderParamProcessor extends AbstractInvocationCollectionProcessor
{

   public HeaderParamProcessor(String paramName)
   {
      super(paramName);
   }

   public HeaderParamProcessor(String paramName, Type type, Annotation[] annotations, ClientConfiguration config)
   {
      super(paramName, type, annotations, config);
   }
   
   @Override
   protected ClientInvocation apply(ClientInvocation invocation, Object object)
   {
       invocation.getHeaders().header(paramName, object);
       return invocation;
   }

}