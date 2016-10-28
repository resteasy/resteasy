package org.jboss.resteasy.test.client.other.resource;

import org.jboss.resteasy.spi.NoLogWebApplicationException;
import org.jboss.resteasy.util.HttpResponseCodes;

public class ApacheHttpClient4ResourceImpl implements ApacheHttpClient4Resource {
    public String get() {
        return "hello world";
    }

    public String error() {
        throw new NoLogWebApplicationException(HttpResponseCodes.SC_NOT_FOUND);
    }

    public String getData(String data) {
        return "Here is your string:" + data;
    }
}
