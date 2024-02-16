package org.jboss.resteasy.test.injection;

import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.injection.resource.ProviderInjectionProviderReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @tpSubChapter Injection tests
 * @tpChapter Unit tests
 * @tpTestCaseDetails Test for correct injection in registered custom provider.
 * @tpSince RESTEasy 3.0.16
 */
public class ProviderInjectionTest {
    private static final String ERROR_MSG = String.format("%s provider was not successfully registered.",
            ProviderInjectionProviderReader.class.getSimpleName());

    /**
     * @tpTestDetails Test for injected HttpHeaders and Providers
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testCacheControl() {
        ProviderInjectionProviderReader reader = new ProviderInjectionProviderReader();
        ResteasyProviderFactory factory = ResteasyProviderFactory.newInstance();
        RegisterBuiltin.register(factory);
        factory.registerProviderInstance(reader);

        Assertions.assertNotNull(reader.headers, ERROR_MSG);
        Assertions.assertNotNull(reader.workers, ERROR_MSG);
    }

}
