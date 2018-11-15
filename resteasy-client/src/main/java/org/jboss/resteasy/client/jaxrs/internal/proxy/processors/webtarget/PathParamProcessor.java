package org.jboss.resteasy.client.jaxrs.internal.proxy.processors.webtarget;

import org.jboss.resteasy.client.jaxrs.internal.ClientConfiguration;
import org.jboss.resteasy.client.jaxrs.internal.proxy.processors.WebTargetProcessor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.client.WebTarget;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PathParamProcessor implements WebTargetProcessor
{
   private final String paramName;
   private final boolean isEncoded;
   protected final Type type;
   protected final Annotation[] annotations;
   protected final ClientConfiguration configuration;

   public PathParamProcessor(final String paramName, final boolean isEncoded, final Type genericType, final Annotation[] annotations, final ClientConfiguration clientConfiguration)
   {
      this.paramName = paramName;
      this.isEncoded = isEncoded;
      this.type = genericType;
      this.annotations = annotations;
      this.configuration = clientConfiguration;
   }

   @Override
   public WebTarget build(WebTarget target, Object param)
   {
      Object param2 = configuration.toString(param, type, annotations);
      return isEncoded
         ? target.resolveTemplateFromEncoded(paramName, param2)
         : target.resolveTemplate(paramName, param2, false);
   }
}
