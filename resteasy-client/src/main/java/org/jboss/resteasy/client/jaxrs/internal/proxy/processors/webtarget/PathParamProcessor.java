package org.jboss.resteasy.client.jaxrs.internal.proxy.processors.webtarget;

import org.jboss.resteasy.client.jaxrs.internal.proxy.processors.WebTargetProcessor;

import javax.ws.rs.client.WebTarget;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PathParamProcessor implements WebTargetProcessor
{
   private final String paramName;
   private final Boolean encodeSlashInPath;

   public PathParamProcessor(String paramName)
   {
      this.paramName = paramName;
      this.encodeSlashInPath = true;
   }

   public PathParamProcessor(String paramName, Boolean encodeSlashInPath)
   {
      this.paramName = paramName;
      this.encodeSlashInPath = encodeSlashInPath;
   }

   @Override
   public WebTarget build(WebTarget target, Object param)
   {
      return target.resolveTemplate(paramName, param, encodeSlashInPath);
   }
}
