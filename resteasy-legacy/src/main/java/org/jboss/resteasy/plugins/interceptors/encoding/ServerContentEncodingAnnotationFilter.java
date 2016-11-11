package org.jboss.resteasy.plugins.interceptors.encoding;

import java.util.Set;

import javax.annotation.Priority;
import javax.ws.rs.ConstrainedTo;
import javax.ws.rs.Priorities;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.ext.WriterInterceptor;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @deprecated Use org.jboss.resteasy.plugins.interceptors.ServerContentEncodingAnnotationFilter instead.
 * @version $Revision: 1 $
 */
@ConstrainedTo(RuntimeType.SERVER)
@Priority(Priorities.HEADER_DECORATOR)
@Deprecated
public class ServerContentEncodingAnnotationFilter extends org.jboss.resteasy.plugins.interceptors.ServerContentEncodingAnnotationFilter implements WriterInterceptor
{
   public ServerContentEncodingAnnotationFilter(Set<String> encodings)
   {
      super(encodings);
   }
}