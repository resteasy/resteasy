package org.jboss.resteasy.test.providers;

import org.jboss.resteasy.plugins.providers.DefaultBooleanWriter;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.providers.resource.ProviderFactoryPrecedenceBase;
import org.jboss.resteasy.test.providers.resource.ProviderFactoryPrecedenceIntegerPlainTextWriter;
import org.jboss.resteasy.test.providers.resource.ProviderFactoryPrecendencePlainTextWriter;
import org.jboss.resteasy.util.Types;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

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
        Assert.assertNull("PlainTextWriter is should be of generic type",
                Types.getTemplateParameterOfInterface(ProviderFactoryPrecendencePlainTextWriter.class, MessageBodyWriter.class));
        Assert.assertEquals("IntegerPlainTextWriter should be Integer type",
                Integer.class, Types.getTemplateParameterOfInterface(ProviderFactoryPrecedenceIntegerPlainTextWriter.class, MessageBodyWriter.class));
        Assert.assertEquals("Concrete class should be Double type",
                Double.class, Types.getTemplateParameterOfInterface(Concrete.class, MessageBodyWriter.class));
        Assert.assertEquals("Concrete2 class should be Double type",
                Boolean.class, Types.getTemplateParameterOfInterface(Concrete2.class, MessageBodyWriter.class));
        Assert.assertEquals("ConcreteMultiple class should be Short type",
                Short.class, Types.getTemplateParameterOfInterface(ConcreteMultiple.class, MessageBodyWriter.class));
    }

    /**
     * @tpTestDetails ResteasyProviderFactory - register default provider instance, test that correct DefaultTextPlain provider is taken
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testMatching2() {
        ResteasyProviderFactory factory = new ResteasyProviderFactory();
        RegisterBuiltin.register(factory);

        MessageBodyWriter<Boolean> writer = factory.getMessageBodyWriter(Boolean.class, null, null, new MediaType("text", "plain"));
        Assert.assertNotNull("No writer exists for the given media type", writer);
        Assert.assertEquals("The type of the writer is incorrect", writer.getClass(), DefaultBooleanWriter.class);
    }

    /**
     * @tpTestDetails ResteasyProviderFactory - register provider instances, test that application providers take precedence over builtin
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testUserPrecendence1() throws Exception {
        // Register Built In first
        ResteasyProviderFactory factory = new ResteasyProviderFactory();
        RegisterBuiltin.register(factory);

        factory.registerProviderInstance(new ProviderFactoryPrecendencePlainTextWriter());

        // Test that application providers take precedence over builtin
        verifyPlainWriter(factory);

        factory.registerProviderInstance(new ProviderFactoryPrecedenceIntegerPlainTextWriter());
        verifyIntegerWriter(factory);

    }

    /**
     * @tpTestDetails ResteasyProviderFactory - register provider instances, verify they are available in user ordered
     * precedence
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testUserPrecendence2() throws Exception {
        // register PlainTextWriter first
        ResteasyProviderFactory factory = new ResteasyProviderFactory();

        factory.registerProviderInstance(new ProviderFactoryPrecendencePlainTextWriter());
        RegisterBuiltin.register(factory);

        verifyPlainWriter(factory);

        factory.registerProviderInstance(new ProviderFactoryPrecedenceIntegerPlainTextWriter());
        verifyIntegerWriter(factory);

    }

    /**
     * @tpTestDetails ResteasyProviderFactory - register provider instances, verify they are available in user ordered
     * precedence
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testUserPrecendence3() throws Exception {
        ResteasyProviderFactory factory = new ResteasyProviderFactory();

        factory.registerProviderInstance(new ProviderFactoryPrecedenceIntegerPlainTextWriter());
        factory.registerProviderInstance(new ProviderFactoryPrecendencePlainTextWriter());
        RegisterBuiltin.register(factory);

        verifyIntegerWriter(factory);

    }

    private void verifyPlainWriter(ResteasyProviderFactory factory) {
        MessageBodyWriter writer2 = factory.getMessageBodyWriter(Character.class, null, null, MediaType.TEXT_PLAIN_TYPE);
        Assert.assertNotNull("No writer exists for the given media type", writer2);
        Assert.assertTrue("The type of the writer is incorrect", writer2 instanceof ProviderFactoryPrecendencePlainTextWriter);
    }

    private void verifyIntegerWriter(ResteasyProviderFactory factory) {
        MessageBodyWriter writer2;
        // Test that type specific template providers take precedence over others
        writer2 = factory.getMessageBodyWriter(Integer.class, null, null, MediaType.TEXT_PLAIN_TYPE);
        Assert.assertNotNull("No writer exists for the given media type", writer2);
        Assert.assertTrue("The type of the writer is incorrect", writer2 instanceof ProviderFactoryPrecedenceIntegerPlainTextWriter);
    }
}
