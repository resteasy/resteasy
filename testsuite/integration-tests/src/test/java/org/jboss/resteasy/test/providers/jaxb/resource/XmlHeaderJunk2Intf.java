package org.jboss.resteasy.test.providers.jaxb.resource;

import org.jboss.resteasy.annotations.Decorator;
import org.junit.Assert;

/**
 * Test correct media type, but incorrect type
 */
@Decorator(processor = XmlHeaderDecorator.class, target = Assert.class)
public @interface XmlHeaderJunk2Intf {
}
