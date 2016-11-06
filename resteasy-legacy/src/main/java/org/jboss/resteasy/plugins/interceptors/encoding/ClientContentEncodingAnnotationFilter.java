package org.jboss.resteasy.plugins.interceptors.encoding;

import javax.annotation.Priority;
import javax.ws.rs.ConstrainedTo;
import javax.ws.rs.Priorities;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.ext.WriterInterceptor;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @deprecated Use org.jboss.resteasy.plugins.interceptors.ClientContentEncodingAnnotationFilter instead.
 * @version $Revision: 1 $
 */
@ConstrainedTo(RuntimeType.CLIENT)
@Priority(Priorities.HEADER_DECORATOR)
@Deprecated
public class ClientContentEncodingAnnotationFilter extends org.jboss.resteasy.plugins.interceptors.ClientContentEncodingAnnotationFilter implements WriterInterceptor
{
   public ClientContentEncodingAnnotationFilter(String encoding)
   {
      super(encoding);
   }
}
