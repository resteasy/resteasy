package org.jboss.resteasy.test.providers.jaxb.resource;

import org.jboss.resteasy.annotations.Decorator;

import jakarta.xml.bind.Marshaller;

/**
 * Test correct type (Marshaller), but incorrect media type
 */
@Decorator(processor = XmlHeaderDecorator.class, target = Marshaller.class)
public @interface XmlHeaderJunkIntf {
}
