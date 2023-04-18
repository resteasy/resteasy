package org.jboss.resteasy.test.client.resource;

import javax.ws.rs.ext.RuntimeDelegate;

public class NullStringBeanHeaderDelegate implements RuntimeDelegate.HeaderDelegate<StringBean> {

    @Override
    public StringBean fromString(String arg0) throws IllegalArgumentException {
        return new StringBean(arg0);
    }

    @Override
    public String toString(StringBean arg0) throws IllegalArgumentException {
        return null;
    }

}
