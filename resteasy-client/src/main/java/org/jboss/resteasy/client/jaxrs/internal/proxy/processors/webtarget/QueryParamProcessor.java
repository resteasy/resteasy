package org.jboss.resteasy.client.jaxrs.internal.proxy.processors.webtarget;

import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.internal.ClientConfiguration;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import jakarta.ws.rs.client.WebTarget;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class QueryParamProcessor extends AbstractWebTargetCollectionProcessor
{
   public QueryParamProcessor(final String paramName)
   {
      super(paramName);
   }

   public QueryParamProcessor(final String paramName, final Type type, final Annotation[] annotations, final ClientConfiguration config)
   {
      super(paramName, type, annotations, config);
   }

   @Override
   protected WebTarget apply(WebTarget target, Object... objects)
   {
      ResteasyWebTarget t = (ResteasyWebTarget)target;
      return t.queryParamNoTemplate(paramName, objects);
   }


}
