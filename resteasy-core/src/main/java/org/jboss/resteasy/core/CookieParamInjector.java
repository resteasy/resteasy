package org.jboss.resteasy.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Type;
import java.util.Set;

import jakarta.ws.rs.core.Cookie;

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
public class CookieParamInjector implements ValueInjector {
    private final RequestParameterExtractor extractor;

    public CookieParamInjector(final Class type, final Type genericType, final AccessibleObject target, final String cookieName,
            final String defaultValue, final Annotation[] annotations, final ResteasyProviderFactory factory) {
        if (type.equals(Cookie.class)) {
            extractor = ParameterExtractors.forCookieParam(type, null, Set.of(), cookieName, defaultValue, factory);

        } else {
            extractor = ParameterExtractors.forCookieParam(type, genericType, Set.of(annotations), cookieName, defaultValue,
                    factory);
        }
    }

    @Override
    public Object inject(HttpRequest request, HttpResponse response, boolean unwrapAsync) {
        return extractor.extract(request);
    }

    @Override
    public Object inject(boolean unwrapAsync) {
        throw new RuntimeException(Messages.MESSAGES.illegalToInjectCookieParam());
    }
}
