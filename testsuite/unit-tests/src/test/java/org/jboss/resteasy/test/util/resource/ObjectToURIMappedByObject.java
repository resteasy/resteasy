package org.jboss.resteasy.test.util.resource;

import org.jboss.resteasy.spi.touri.MappedBy;

@MappedBy(resource = ObjectToURIResouce.class, method = "getFoo")
public class ObjectToURIMappedByObject extends ObjectToURIAbstractURITemplateObject {
    public ObjectToURIMappedByObject(final int id) {
        super(id);
    }
}
