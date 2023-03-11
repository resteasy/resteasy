package org.jboss.resteasy.core;

import java.util.List;

import jakarta.ws.rs.core.MultivaluedMap;

import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.PrefixedFormFieldsHttpRequest;

/**
 * Extension of {@link FormInjector} that handles prefixes for associated classes.
 */
public class PrefixedFormInjector extends FormInjector {

    private final String prefix;

    /**
     * Constructor setting the prefix.
     *
     * @param type    type class
     * @param prefix  prefix
     * @param factory provider factory
     */
    public PrefixedFormInjector(final Class type, final String prefix, final ResteasyProviderFactory factory) {
        super(type, factory);
        this.prefix = prefix;
    }

    /**
     * {@inheritDoc} Wraps the request in a
     */
    @Override
    public Object inject(HttpRequest request, HttpResponse response, boolean unwrapAsync) {
        if (!containsPrefixedFormFieldsWithValue(request.getDecodedFormParameters())) {
            return null;
        }
        return doInject(prefix, request, response, unwrapAsync);
    }

    /**
     * Calls the super {@link #inject(org.jboss.resteasy.spi.HttpRequest, org.jboss.resteasy.spi.HttpResponse, boolean)} method.
     *
     * @param prefix      prefix
     * @param request     http request
     * @param response    http response
     * @param unwrapAsync unwrap async
     * @return injector instance
     */
    protected Object doInject(String prefix, HttpRequest request, HttpResponse response, boolean unwrapAsync) {
        return super.inject(new PrefixedFormFieldsHttpRequest(prefix, request), response, unwrapAsync);
    }

    /**
     * Checks to see if the decodedParameters contains any form fields starting with the prefix. Also checks if the value is not
     * empty.
     *
     * @param decodedFormParameters decoded parameters map
     * @return boolean result
     */
    private boolean containsPrefixedFormFieldsWithValue(MultivaluedMap<String, String> decodedFormParameters) {
        for (String parameterName : decodedFormParameters.keySet()) {
            if (parameterName.startsWith(prefix)) {
                if (hasValue(decodedFormParameters.get(parameterName))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks that the list has an non empty value.
     *
     * @param list list of values
     * @return true if the list contains values
     */
    protected boolean hasValue(List<String> list) {
        return !list.isEmpty() && list.get(0).length() > 0;
    }
}
