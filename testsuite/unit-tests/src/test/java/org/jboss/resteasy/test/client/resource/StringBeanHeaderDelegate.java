package org.jboss.resteasy.test.client.resource;

import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.RuntimeDelegate.HeaderDelegate;

@Provider
public class StringBeanHeaderDelegate implements HeaderDelegate<StringBean> {

    @Override
    public StringBean fromString(String string) throws IllegalArgumentException {
        return new StringBean(string);
    }

    @Override
    public String toString(StringBean bean) throws IllegalArgumentException {
        return bean.get();
    }

}
