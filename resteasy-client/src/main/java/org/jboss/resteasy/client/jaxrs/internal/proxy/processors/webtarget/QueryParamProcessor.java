package org.jboss.resteasy.client.jaxrs.internal.proxy.processors.webtarget;

import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.internal.ClientConfiguration;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.client.WebTarget;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class QueryParamProcessor extends AbstractWebTargetCollectionProcessor
{
   public QueryParamProcessor(String paramName)
   {
      super(paramName);
   }

   public QueryParamProcessor(String paramName, Type type, Annotation[] annotations, ClientConfiguration config)
   {
      super(paramName, type, annotations, config);
   }
   
   @Override
   protected WebTarget apply(WebTarget target, Object object)
   {
      ResteasyWebTarget t = (ResteasyWebTarget)target;
      return t.queryParamNoTemplate(paramName, object);
   }


}
