package org.jboss.resteasy.test.injection.resource;

import java.util.List;

public class StringParameterInjectorGenericType<T> {
    public StringParameterInjectorGenericType(final String ignore) {
    }

    public List<StringParameterInjectorGenericType<T>> returnSomething() {
        return null;
    }
}
