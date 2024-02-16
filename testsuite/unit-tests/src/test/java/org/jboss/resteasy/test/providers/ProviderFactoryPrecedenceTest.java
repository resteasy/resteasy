package org.jboss.resteasy.test.providers;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;

import org.jboss.resteasy.plugins.providers.DefaultBooleanWriter;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.util.Types;
import org.jboss.resteasy.test.providers.resource.ProviderFactoryPrecedenceBase;
import org.jboss.resteasy.test.providers.resource.ProviderFactoryPrecedenceIntegerPlainTextWriter;
import org.jboss.resteasy.test.providers.resource.ProviderFactoryPrecendencePlainTextWriter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @tpSubChapter Providers
 * @tpChapter Unit tests
 * @tpSince RESTEasy 3.0.16
 */
public class ProviderFactoryPrecedenceTest {

    @Provider
    @Produces("text/plain")
    public static class Concrete extends ProviderFactoryPrecedenceBase<Double> {
    }

    @Provider
    @Produces("text/plain")
    public static class Concrete2 extends ProviderFactoryPrecedenceBase<Boolean> {
    }

    public static class BaseMultiple<V, X> extends ProviderFactoryPrecedenceBase<X> {
    }

    public static class ConcreteMultiple extends BaseMultiple<String, Short> {
    }

    /**
     * @tpTestDetails Test that classes which extends generic MessageBodyWriter of generic type are of the correct type
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testTypes() {
        Assertions.assertNull(Types.getTemplateParameterOfInterface(ProviderFactoryPrecendencePlainTextWriter.class,
                MessageBodyWriter.class),
                "PlainTextWriter is should be of generic type");
        Assertions.assertEquals(Integer.class,
                Types.getTemplateParameterOfInterface(ProviderFactoryPrecedenceIntegerPlainTextWriter.class,
                        MessageBodyWriter.class),
                "IntegerPlainTextWriter should be Integer type");
        Assertions.assertEquals(Double.class, Types.getTemplateParameterOfInterface(Concrete.class, MessageBodyWriter.class),
                "Concrete class should be Double type");
        Assertions.assertEquals(Boolean.class, Types.getTemplateParameterOfInterface(Concrete2.class, MessageBodyWriter.class),
                "Concrete2 class should be Double type");
        Assertions.assertEquals(Short.class,
                Types.getTemplateParameterOfInterface(ConcreteMultiple.class, MessageBodyWriter.class),
                "ConcreteMultiple class should be Short type");
    }

    /**
     * @tpTestDetails ResteasyProviderFactory - register default provider instance, test that correct DefaultTextPlain provider
     *                is taken
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMatching2() {
        ResteasyProviderFactory factory = ResteasyProviderFactory.newInstance();
        RegisterBuiltin.register(factory);

        MessageBodyWriter<Boolean> writer = factory.getMessageBodyWriter(Boolean.class, null, null,
                new MediaType("text", "plain"));
        Assertions.assertNotNull(writer, "No writer exists for the given media type");
        Assertions.assertEquals(writer.getClass(), DefaultBooleanWriter.class, "The type of the writer is incorrect");
    }

    /**
     * @tpTestDetails ResteasyProviderFactory - register provider instances, test that application providers take precedence
     *                over builtin
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testUserPrecendence1() throws Exception {
        // Register Built In first
        ResteasyProviderFactory factory = ResteasyProviderFactory.newInstance();
        RegisterBuiltin.register(factory);

        factory.registerProviderInstance(new ProviderFactoryPrecendencePlainTextWriter());

        // Test that application providers take precedence over builtin
        verifyPlainWriter(factory);

        factory.registerProviderInstance(new ProviderFactoryPrecedenceIntegerPlainTextWriter());
        verifyIntegerWriter(factory);

    }

    /**
     * @tpTestDetails ResteasyProviderFactory - register provider instances, verify they are available in user ordered
     *                precedence
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testUserPrecendence2() throws Exception {
        // register PlainTextWriter first
        ResteasyProviderFactory factory = ResteasyProviderFactory.newInstance();

        factory.registerProviderInstance(new ProviderFactoryPrecendencePlainTextWriter());
        RegisterBuiltin.register(factory);

        verifyPlainWriter(factory);

        factory.registerProviderInstance(new ProviderFactoryPrecedenceIntegerPlainTextWriter());
        verifyIntegerWriter(factory);

    }

    /**
     * @tpTestDetails ResteasyProviderFactory - register provider instances, verify they are available in user ordered
     *                precedence
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testUserPrecendence3() throws Exception {
        ResteasyProviderFactory factory = ResteasyProviderFactory.newInstance();

        factory.registerProviderInstance(new ProviderFactoryPrecedenceIntegerPlainTextWriter());
        factory.registerProviderInstance(new ProviderFactoryPrecendencePlainTextWriter());
        RegisterBuiltin.register(factory);

        verifyIntegerWriter(factory);

    }

    private void verifyPlainWriter(ResteasyProviderFactory factory) {
        MessageBodyWriter writer2 = factory.getMessageBodyWriter(Character.class, null, null, MediaType.TEXT_PLAIN_TYPE);
        Assertions.assertNotNull(writer2, "No writer exists for the given media type");
        Assertions.assertTrue(writer2 instanceof ProviderFactoryPrecendencePlainTextWriter,
                "The type of the writer is incorrect");
    }

    private void verifyIntegerWriter(ResteasyProviderFactory factory) {
        MessageBodyWriter writer2;
        // Test that type specific template providers take precedence over others
        writer2 = factory.getMessageBodyWriter(Integer.class, null, null, MediaType.TEXT_PLAIN_TYPE);
        Assertions.assertNotNull(writer2, "No writer exists for the given media type");
        Assertions.assertTrue(writer2 instanceof ProviderFactoryPrecedenceIntegerPlainTextWriter,
                "The type of the writer is incorrect");
    }
}
