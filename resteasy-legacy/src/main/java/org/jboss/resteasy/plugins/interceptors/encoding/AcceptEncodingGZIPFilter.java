package org.jboss.resteasy.plugins.interceptors.encoding;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.ext.Provider;

/**
 * Must be used in conjunction with GZIPDecodingInterceptor
 * <p>
 * Sets
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @deprecated Use org.jboss.resteasy.plugins.interceptors.AcceptEncodingGZIPFilter instead
 * @version $Revision: 1 $
 */
@Provider
@Priority(Priorities.HEADER_DECORATOR)
@Deprecated
public class AcceptEncodingGZIPFilter extends org.jboss.resteasy.plugins.interceptors.AcceptEncodingGZIPFilter implements ClientRequestFilter
{
}
