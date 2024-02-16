package org.jboss.resteasy.test.providers.jaxb.resource;

import org.jboss.resteasy.annotations.Decorator;
import org.junit.jupiter.api.Assertions;

/**
 * Test correct media type, but incorrect type
 */
@Decorator(processor = XmlHeaderDecorator.class, target = Assertions.class)
public @interface XmlHeaderJunk2Intf {
}
