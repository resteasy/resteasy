package org.jboss.resteasy.test.providers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import jakarta.annotation.Priority;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.ParamConverter;
import jakarta.ws.rs.ext.ParamConverterProvider;
import jakarta.ws.rs.ext.Provider;

import org.jboss.resteasy.core.providerfactory.ResteasyProviderFactoryImpl;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @tpSubChapter Providers
 * @tpChapter Unit tests
 * @tpTestCaseDetails Correct storage of ParamConverterProviders and ExceptionMappers of equal @Priority
 * @tpSince RESTEasy 4.0.0
 */
public class PriorityEqualityTest {

    public static class TestException extends Exception {
        private static final long serialVersionUID = 1L;
    }

    @Provider
    @Priority(20)
    public static class ExceptionMapper1 implements ExceptionMapper<TestException> {

        @Override
        public Response toResponse(TestException exception) {
            return Response.ok().status(444).entity("1").build();
        }
    }

    @Provider
    @Priority(20)
    public static class ExceptionMapper2 implements ExceptionMapper<TestException> {

        @Override
        public Response toResponse(TestException exception) {
            return Response.ok().status(444).entity("BBB").build();
        }
    }

    public static class Foo {
        private String foo;

        public Foo(final String foo) {
            this.foo = foo;
        }

        public String getFoo() {
            return foo;
        }
    }

    public static class FooParamConverter implements ParamConverter<Foo> {
        private String foo;

        public FooParamConverter(final String foo) {
            this.foo = foo;
        }

        @Override
        public Foo fromString(String value) {
            return new Foo(foo);
        }

        @Override
        public String toString(Foo value) {
            return value.getFoo();
        }
    }

    @Provider
    @Priority(20)
    public static class ParamConverterProvider1 implements ParamConverterProvider {

        @SuppressWarnings("unchecked")
        @Override
        public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
            return (ParamConverter<T>) new FooParamConverter("1");
        }
    }

    @Provider
    @Priority(20)
    public static class ParamConverterProvider2 implements ParamConverterProvider {

        @SuppressWarnings("unchecked")
        @Override
        public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
            return (ParamConverter<T>) new FooParamConverter("2");
        }
    }

    //////////////////////////////////////////////////////////////////////////////

    /**
     * @tpTestDetails ResteasyProviderFactory should store multiple ParamConvertProviders
     *                with the same @Priority.
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testParamConverterProvidersFromClass() throws Exception {
        ResteasyProviderFactory factory = ResteasyProviderFactory.newInstance();
        RegisterBuiltin.register(factory);
        ResteasyProviderFactory.setInstance(factory);

        factory.register(ParamConverterProvider1.class);
        factory.register(ParamConverterProvider2.class);
        Assertions.assertTrue(factory.getProviderClasses().contains(ParamConverterProvider1.class));
        Assertions.assertTrue(factory.getProviderClasses().contains(ParamConverterProvider2.class));
        ResteasyProviderFactory.clearInstanceIfEqual(factory);
    }

    /**
     * @tpTestDetails ResteasyProviderFactory should store multiple ParamConvertProviders
     *                with the same @Priority.
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testParamConverterProvidersObjects() throws Exception {
        ResteasyProviderFactory factory = ResteasyProviderFactory.newInstance();
        RegisterBuiltin.register(factory);
        ResteasyProviderFactory.setInstance(factory);

        ParamConverterProvider p1 = new ParamConverterProvider1();
        ParamConverterProvider p2 = new ParamConverterProvider2();
        factory.registerProviderInstance(p1);
        factory.registerProviderInstance(p2);
        Assertions.assertTrue(factory.getProviderInstances().contains(p1));
        Assertions.assertTrue(factory.getProviderInstances().contains(p2));

        ResteasyProviderFactory.clearInstanceIfEqual(factory);
    }

    /**
     * @tpTestDetails ResteasyProviderFactory should store a single ExceptionMapper for
     *                a given Exception and @Priority.
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testExceptionMappersFromClass() throws Exception {
        ResteasyProviderFactoryImpl factory = new ResteasyProviderFactoryImpl();
        factory.register(ExceptionMapper1.class);
        factory.register(ExceptionMapper2.class);
        Assertions.assertEquals(ExceptionMapper2.class, factory.getExceptionMapper(TestException.class).getClass());
    }

    /**
     * @tpTestDetails ResteasyProviderFactory should store a single ExceptionMapper for
     *                a given Exception and @Priority.
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testExceptionObjects() throws Exception {
        ResteasyProviderFactoryImpl factory = new ResteasyProviderFactoryImpl();
        factory.registerProviderInstance(new ExceptionMapper1());
        factory.registerProviderInstance(new ExceptionMapper2());
        Assertions.assertEquals(ExceptionMapper2.class, factory.getExceptionMapper(TestException.class).getClass());
    }
}
