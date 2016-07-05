package org.jboss.resteasy.test.util.resource;

import org.jboss.resteasy.spi.touri.URIable;

public class ObjectToURIableObject implements URIable {
    public String toURI() {
        return "/my-url";
    }
}
