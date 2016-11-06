package org.jboss.resteasy.plugins.interceptors.encoding;

import javax.ws.rs.ConstrainedTo;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.container.DynamicFeature;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @deprecated Use org.jboss.resteasy.plugins.interceptors.ServerContentEncodingAnnotationFeature instead.
 * @version $Revision: 1 $
 */
@ConstrainedTo(RuntimeType.SERVER)
@Deprecated
public class ServerContentEncodingAnnotationFeature extends org.jboss.resteasy.plugins.interceptors.ServerContentEncodingAnnotationFeature implements DynamicFeature
{
}
