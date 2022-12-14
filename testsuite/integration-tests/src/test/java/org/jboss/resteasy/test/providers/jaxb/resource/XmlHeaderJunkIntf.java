package org.jboss.resteasy.test.providers.jaxb.resource;

import jakarta.xml.bind.Marshaller;

import org.jboss.resteasy.annotations.Decorator;

/**
 * Test correct type (Marshaller), but incorrect media type
 */
@Decorator(processor = XmlHeaderDecorator.class, target = Marshaller.class)
public @interface XmlHeaderJunkIntf {
}
