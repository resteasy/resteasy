package org.jboss.resteasy.plugins.interceptors.encoding;

import javax.ws.rs.ConstrainedTo;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.container.DynamicFeature;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @deprecated Use org.jboss.resteasy.plugins.interceptors.ClientContentEncodingAnnotationFeature instead.
 * @version $Revision: 1 $
 */
@ConstrainedTo(RuntimeType.CLIENT)
@Deprecated
public class ClientContentEncodingAnnotationFeature extends org.jboss.resteasy.plugins.interceptors.ClientContentEncodingAnnotationFeature implements DynamicFeature
{
}
