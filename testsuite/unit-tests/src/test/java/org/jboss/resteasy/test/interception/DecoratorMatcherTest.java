package org.jboss.resteasy.test.interception;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.atomic.AtomicBoolean;

import jakarta.ws.rs.core.MediaType;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.jboss.resteasy.annotations.Decorator;
import org.jboss.resteasy.core.interception.jaxrs.DecoratorMatcher;
import org.jboss.resteasy.spi.DecoratorProcessor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @tpSubChapter Interception tests
 * @tpChapter Unit tests
 * @tpTestCaseDetails Test for org.jboss.resteasy.annotations.Decorator class.
 * @tpSince RESTEasy 3.0.16
 */
public class DecoratorMatcherTest {
    private static final AtomicBoolean called = new AtomicBoolean(false);
    private JAXBContext jaxbContext;
    private DecoratorMatcher decoratorMatcher;

    @BeforeEach
    public void init() throws JAXBException {
        called.set(false);
        jaxbContext = JAXBContext.newInstance(AnObject.class);
        decoratorMatcher = new DecoratorMatcher();
    }

    /**
     * @tpTestDetails Decorator method should not be called in this test.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void shouldNotThrowOnUnmarshaller() throws JAXBException {
        decoratorMatcher.decorate(Unmarshaller.class, jaxbContext.createUnmarshaller(), AnObject.class, new Annotation[0],
                MediaType.APPLICATION_XML_TYPE);
        Assertions.assertFalse(called.get(), "Decorate method was called");
    }

    /**
     * @tpTestDetails Decorator method should be called in this test.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void shouldCallOnMarshaller() throws JAXBException {
        decoratorMatcher.decorate(Marshaller.class, jaxbContext.createMarshaller(), AnObject.class, new Annotation[0],
                MediaType.APPLICATION_XML_TYPE);
        Assertions.assertTrue(called.get(), "Decorate method was not called");
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Decorator(processor = MarshallerDecorator.Processor.class, target = Marshaller.class)
    public @interface MarshallerDecorator {
        class Processor implements DecoratorProcessor<Marshaller, MarshallerDecorator> {
            @Override
            public Marshaller decorate(Marshaller target, MarshallerDecorator annotation, Class type, Annotation[] annotations,
                    MediaType mediaType) {
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
}
