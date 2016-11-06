package org.jboss.resteasy.test.resource.param.resource;

import javax.ws.rs.ext.RuntimeDelegate;

public class HeaderDelegateDelegate<T> implements HeaderDelegateInterface4, RuntimeDelegate.HeaderDelegate<T> {
    @Override
    public T fromString(String value) {
        return null;
    }

    @Override
    public String toString(T value) {
        return null;
    }
}
