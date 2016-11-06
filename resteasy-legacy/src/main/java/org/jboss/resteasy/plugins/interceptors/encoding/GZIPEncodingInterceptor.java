package org.jboss.resteasy.plugins.interceptors.encoding;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.WriterInterceptor;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @deprecated Use org.jboss.resteasy.plugins.interceptors.GZIPEncodingInterceptor instead.
 * @version $Revision: 1 $
 */
@Provider
@Priority(Priorities.ENTITY_CODER)
@Deprecated
public class GZIPEncodingInterceptor extends org.jboss.resteasy.plugins.interceptors.GZIPEncodingInterceptor implements WriterInterceptor
{
}
