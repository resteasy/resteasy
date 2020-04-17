package org.jboss.resteasy.client.jaxrs.internal.proxy.processors.webtarget;

import org.jboss.resteasy.client.jaxrs.i18n.Messages;
import org.jboss.resteasy.client.jaxrs.internal.proxy.processors.WebTargetProcessor;

import java.util.Objects;

import javax.ws.rs.PathParam;
import javax.ws.rs.client.WebTarget;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PathParamProcessor implements WebTargetProcessor
{
   private final String paramName;
   private final Boolean encodeSlashInPath;

   public PathParamProcessor(final String paramName)
   {
      this.paramName = paramName;
      this.encodeSlashInPath = true;
   }

   public PathParamProcessor(final String paramName, final Boolean encodeSlashInPath)
   {
      this.paramName = paramName;
      this.encodeSlashInPath = encodeSlashInPath;
   }

   @Override
   public WebTarget build(WebTarget target, Object param)
   {
      return target.resolveTemplate(paramName, Objects.requireNonNull(param, Messages.MESSAGES.nullParameter(PathParam.class.getSimpleName())), encodeSlashInPath);
   }
}
