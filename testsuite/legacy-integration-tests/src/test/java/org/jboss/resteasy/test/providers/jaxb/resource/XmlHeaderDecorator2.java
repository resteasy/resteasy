package org.jboss.resteasy.test.providers.jaxb.resource;

import org.jboss.resteasy.annotations.DecorateTypes;
import org.jboss.resteasy.spi.interception.DecoratorProcessor;
import org.junit.Assert;

import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;

@DecorateTypes("application/xml")
public class XmlHeaderDecorator2 implements DecoratorProcessor<Assert, XmlHeaderJunk2Intf> {
   public Assert decorate(Assert target, XmlHeaderJunk2Intf annotation, Class type, Annotation[] annotations, MediaType mediaType) {
      throw new RuntimeException("FAILURE!!!!");
   }
}
