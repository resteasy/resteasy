package org.jboss.resteasy.test.providers.jaxb.resource;

import java.lang.annotation.Annotation;

import jakarta.ws.rs.core.MediaType;
import jakarta.xml.bind.Marshaller;

import org.jboss.resteasy.annotations.DecorateTypes;
import org.jboss.resteasy.spi.DecoratorProcessor;

@DecorateTypes("application/json")
public class XmlHeaderDecorator implements DecoratorProcessor<Marshaller, XmlHeaderJunkIntf> {
    public Marshaller decorate(Marshaller target, XmlHeaderJunkIntf annotation, Class type, Annotation[] annotations,
            MediaType mediaType) {
        throw new RuntimeException("FAILURE!!!!");
    }
}
