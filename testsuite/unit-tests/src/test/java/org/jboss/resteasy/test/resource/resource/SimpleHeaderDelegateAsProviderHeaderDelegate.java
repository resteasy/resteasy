package org.jboss.resteasy.test.resource.resource;

import javax.ws.rs.ext.RuntimeDelegate.HeaderDelegate;

public class SimpleHeaderDelegateAsProviderHeaderDelegate implements
        HeaderDelegate<SimpleHeaderDelegateAsProviderHeader> {

    @Override
    public SimpleHeaderDelegateAsProviderHeader fromString(String value) {
        throw new RuntimeException("Force error");
        /**
        int i = value.indexOf(";");
        SimpleHeaderDelegateAsProviderHeader th = null;
        if (i < 0) {
            th = new SimpleHeaderDelegateAsProviderHeader("fromString:" + value, "");
        } else {
            th = new SimpleHeaderDelegateAsProviderHeader("fromString:" + value.substring(0, i), value.substring(i + 1));
        }
        return th;
        **/
    }

    @Override
    public String toString(SimpleHeaderDelegateAsProviderHeader value) {
        return "toString:" + value.getMajor() + ";" + value.getMinor();
    }
}
