package org.jboss.resteasy.test.providers;

import jakarta.ws.rs.ext.RuntimeDelegate.HeaderDelegate;

import org.jboss.resteasy.core.providerfactory.ResteasyProviderFactoryImpl;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @tpSubChapter Providers
 * @tpChapter Unit tests
 * @tpTestCaseDetails Registering HeaderDelegate as provider
 * @tpSince RESTEasy 4.0.0
 */
public class HeaderDelegateTest {

    public static class TestHeader {
    }

    public static class TestHeaderDelegate implements HeaderDelegate<TestHeader> {

        @Override
        public TestHeader fromString(String value) {
            return new TestHeader();
        }

        @Override
        public String toString(TestHeader value) {
            return "";
        }
    }

    //////////////////////////////////////////////////////////////////////////////
    /**
     * @tpTestDetails Register HeaderDelegate class
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testProviderClass() {
        ResteasyProviderFactory factory = new ResteasyProviderFactoryImpl();
        factory.register(TestHeaderDelegate.class);
        HeaderDelegate<?> delegate = factory.getHeaderDelegate(TestHeader.class);
        Assertions.assertTrue(delegate instanceof TestHeaderDelegate);
    }

    /**
     * @tpTestDetails Register HeaderDelegate object
     * @tpSince RESTEasy 4.0.0
     */
    @Test
    public void testProviderObject() {
        ResteasyProviderFactory factory = new ResteasyProviderFactoryImpl();
        factory.register(new TestHeaderDelegate());
        HeaderDelegate<?> delegate = factory.getHeaderDelegate(TestHeader.class);
        Assertions.assertTrue(delegate instanceof TestHeaderDelegate);
    }
}