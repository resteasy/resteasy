package org.jboss.resteasy.test.providers.jaxb.resource;

import java.lang.annotation.Annotation;

import jakarta.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.DecorateTypes;
import org.jboss.resteasy.spi.DecoratorProcessor;
import org.junit.jupiter.api.Assertions;

@DecorateTypes("application/xml")
public class XmlHeaderDecorator2 implements DecoratorProcessor<Assertions, XmlHeaderJunk2Intf> {
    public Assertions decorate(Assertions target, XmlHeaderJunk2Intf annotation, Class type, Annotation[] annotations,
            MediaType mediaType) {
        throw new RuntimeException("FAILURE!!!!");
    }
}
