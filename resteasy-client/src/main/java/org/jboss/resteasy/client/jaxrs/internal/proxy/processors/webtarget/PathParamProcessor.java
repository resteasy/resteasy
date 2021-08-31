package org.jboss.resteasy.client.jaxrs.internal.proxy.processors.webtarget;

import org.jboss.resteasy.client.jaxrs.i18n.Messages;
import org.jboss.resteasy.client.jaxrs.internal.ClientConfiguration;
import org.jboss.resteasy.client.jaxrs.internal.proxy.processors.WebTargetProcessor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Objects;

import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.client.WebTarget;

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

   public PathParamProcessor(final String paramName, final Boolean encodeSlashInPath, final Type genericType, final Annotation[] annotations, final ClientConfiguration clientConfiguration)
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
      Object param2 = configuration.toString(Objects.requireNonNull(param, Messages.MESSAGES.nullParameter(PathParam.class.getSimpleName())), type, annotations);
      return target.resolveTemplate(paramName, param2, encodeSlashInPath);
   }
}
