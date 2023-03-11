package org.jboss.resteasy.client.jaxrs.internal.proxy.processors.webtarget;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import jakarta.ws.rs.client.WebTarget;

import org.jboss.resteasy.client.jaxrs.internal.ClientConfiguration;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MatrixParamProcessor extends AbstractWebTargetCollectionProcessor {

    public MatrixParamProcessor(final String paramName) {
        super(paramName);
    }

    public MatrixParamProcessor(final String paramName, final Type type, final Annotation[] annotations,
            final ClientConfiguration config) {
        super(paramName, type, annotations, config);
    }

    @Override
    protected WebTarget apply(WebTarget target, Object... objects) {
        return target.matrixParam(paramName, objects);
    }

}
