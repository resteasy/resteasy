package org.jboss.resteasy.plugins.interceptors.encoding;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.ReaderInterceptor;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @deprecated Use org.jboss.resteasy.plugins.interceptors.GZIPDecodingInterceptor instead.
 * @version $Revision: 1 $
 */
@Provider
@Priority(Priorities.ENTITY_CODER)
@Deprecated
public class GZIPDecodingInterceptor extends org.jboss.resteasy.plugins.interceptors.GZIPDecodingInterceptor implements ReaderInterceptor
{
  public GZIPDecodingInterceptor(int maxSize)
  {
     super(maxSize);
  }
  
  public GZIPDecodingInterceptor()
  {
     super();
  }
}
