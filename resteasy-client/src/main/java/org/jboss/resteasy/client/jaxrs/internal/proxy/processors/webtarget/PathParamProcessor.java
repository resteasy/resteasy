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
   private final Boolean encodeSlashInPath;
   protected Type type;
   protected Annotation[] annotations;
   protected ClientConfiguration configuration;
   
   public PathParamProcessor(String paramName, Boolean encodeSlashInPath, Type genericType, Annotation[] annotations, ClientConfiguration clientConfiguration)
   {
      this.paramName = paramName;
      this.encodeSlashInPath = encodeSlashInPath;
      this.type = genericType;
      this.annotations = annotations;
      this.configuration = clientConfiguration;
   }
   
   @Override
   public WebTarget build(WebTarget target, Object param)
   {
      Object param2 = configuration.toString(param, type, annotations);
      return target.resolveTemplate(paramName, param2, encodeSlashInPath);
   }
}
