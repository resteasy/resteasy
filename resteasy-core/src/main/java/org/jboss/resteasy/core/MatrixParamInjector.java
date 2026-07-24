package org.jboss.resteasy.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Type;
import java.util.Set;

import org.jboss.resteasy.core.extractors.ParameterExtractors;
import org.jboss.resteasy.core.extractors.RequestParameterExtractor;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.ValueInjector;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MatrixParamInjector implements ValueInjector {
    private final RequestParameterExtractor extractor;

    public MatrixParamInjector(final Class type, final Type genericType, final AccessibleObject target, final String paramName,
            final String defaultValue, final boolean encode, final Annotation[] annotations,
            final ResteasyProviderFactory factory) {
        this.extractor = ParameterExtractors.forMatrixParam(type, genericType, Set.of(annotations), encode, paramName,
                defaultValue,
                factory);
    }

    @Override
    public Object inject(HttpRequest request, HttpResponse response, boolean unwrapAsync) {
        return extractor.extract(request);
    }

    @Override
    public Object inject(boolean unwrapAsync) {
        throw new RuntimeException(Messages.MESSAGES.illegalToInjectMatrixParam());
    }
}
