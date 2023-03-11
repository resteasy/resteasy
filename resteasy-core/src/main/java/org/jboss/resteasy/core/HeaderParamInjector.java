package org.jboss.resteasy.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Type;
import java.util.List;

import jakarta.ws.rs.HeaderParam;

import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.ValueInjector;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class HeaderParamInjector extends StringParameterInjector implements ValueInjector {

    public HeaderParamInjector(final Class type, final Type genericType, final AccessibleObject target, final String header,
            final String defaultValue, final Annotation[] annotations, final ResteasyProviderFactory factory) {
        super(type, genericType, header, HeaderParam.class, defaultValue, target, annotations, factory);
    }

    @Override
    public Object inject(HttpRequest request, HttpResponse response, boolean unwrapAsync) {
        List<String> list = request.getHttpHeaders().getRequestHeaders().get(paramName);
        return extractValues(list);
    }

    @Override
    public Object inject(boolean unwrapAsync) {
        throw new RuntimeException(Messages.MESSAGES.illegalToInjectHeaderParam());
    }
}
