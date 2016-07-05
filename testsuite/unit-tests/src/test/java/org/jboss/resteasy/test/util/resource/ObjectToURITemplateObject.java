package org.jboss.resteasy.test.util.resource;

import org.jboss.resteasy.spi.touri.URITemplate;

@URITemplate("/foo/{id}")
public class ObjectToURITemplateObject extends ObjectToURIAbstractURITemplateObject {
    public ObjectToURITemplateObject(final int id) {
        super(id);
    }
}
