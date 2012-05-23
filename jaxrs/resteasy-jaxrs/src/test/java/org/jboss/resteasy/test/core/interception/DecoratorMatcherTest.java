package org.jboss.resteasy.test.core.interception;

import org.jboss.resteasy.annotations.Decorator;
import org.jboss.resteasy.core.interception.DecoratorMatcher;
import org.jboss.resteasy.spi.interception.DecoratorProcessor;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.atomic.AtomicBoolean;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class DecoratorMatcherTest {
	private static final AtomicBoolean called = new AtomicBoolean(false);
	private JAXBContext jaxbContext;
	private DecoratorMatcher decoratorMatcher;

	@Retention(RetentionPolicy.RUNTIME)
	@Decorator(processor = MarshallerDecorator.Processor.class, target = Marshaller.class)
	public static @interface MarshallerDecorator {
		public static class Processor implements DecoratorProcessor<Marshaller, MarshallerDecorator> {
			@Override
			public Marshaller decorate(Marshaller target, MarshallerDecorator annotation, Class type, Annotation[] annotations, MediaType mediaType) {
				called.set(true);
				return target;
			}
		}
	}

	@MarshallerDecorator
	@XmlRootElement
	public static class AnObject {
		private String something;
	}

	@Before
	public void init() throws JAXBException {
		called.set(false);
		jaxbContext = JAXBContext.newInstance(AnObject.class);
		decoratorMatcher = new DecoratorMatcher();
	}

	@Test
	public void shouldNotThrowOnUnmarshaller() throws JAXBException {
		decoratorMatcher.decorate(Unmarshaller.class, jaxbContext.createUnmarshaller(), AnObject.class, new Annotation[0], MediaType.APPLICATION_XML_TYPE);
		assertFalse(called.get());
	}

	@Test
	public void shouldCallOnMarshaller() throws JAXBException {
		decoratorMatcher.decorate(Marshaller.class, jaxbContext.createMarshaller(), AnObject.class, new Annotation[0], MediaType.APPLICATION_XML_TYPE);
		assertTrue(called.get());
	}
}
