package org.jboss.resteasy.client.jaxrs.internal.proxy.processors.invocation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import org.jboss.resteasy.client.jaxrs.internal.ClientConfiguration;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class HeaderParamProcessor extends AbstractInvocationCollectionProcessor {

    public HeaderParamProcessor(final String paramName) {
        super(paramName);
    }

    public HeaderParamProcessor(final String paramName, final Type type, final Annotation[] annotations,
            final ClientConfiguration config) {
        super(paramName, type, annotations, config);
    }

    @Override
    protected ClientInvocation apply(ClientInvocation invocation, Object... objects) {
        for (Object object : objects) {
            String value = invocation.getClientConfiguration().toString(object);
            invocation.getHeaders().header(paramName, value);
        }
        return invocation;
    }

}
