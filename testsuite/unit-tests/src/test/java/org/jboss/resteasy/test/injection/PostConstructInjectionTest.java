package org.jboss.resteasy.test.injection;

import jakarta.annotation.PostConstruct;

import org.jboss.resteasy.spi.util.Types;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @tpSubChapter Test checking for @PostConstruct methods
 * @tpChapter Unit tests
 * @tpTestCaseDetails Tests for org.jboss.resteasy.util.Types.hasPostConstruct(). RESTEASY-2227
 * @tpSince RESTEasy 3.7.0
 */
public class PostConstructInjectionTest {

    public static class TC0 {
        @PostConstruct
        public void m() {
        }
    }

    public static class TC1 {
        @PostConstruct
        public void m(int i) {
        }
    }

    public static class TC2 {
        @PostConstruct
        public int m() {
            return 0;
        }
    }

    public static class TC3 {
        @PostConstruct
        public void m() throws Exception {
            return;
        }
    }

    public static class TC4 {
        @PostConstruct
        public static void m() {
            return;
        }
    }

    public static class TC5 {
        @PostConstruct
        private void m() {
            return;
        }
    }

    public static class TC6 extends TC5 {
        // empty on purpose - should inherit @PostConstruct
    }

    /**
     * @tpTestDetails Test valid @PostContruct method
     * @tpSince RESTEasy 3.7.0
     */
    @Test
    public void testValid() throws Exception {
        Assertions.assertTrue(Types.hasPostConstruct(TC0.class));
    }

    /**
     * @tpTestDetails Test invalid @PostContruct method with > 0 parameters
     * @tpSince RESTEasy 3.7.0
     */
    @Test
    public void testNumberOfParameters() {
        Assertions.assertFalse(Types.hasPostConstruct(TC1.class));
    }

    /**
     * @tpTestDetails Test invalid @PostContruct method with non Void return type
     * @tpSince RESTEasy 3.7.0
     */
    @Test
    public void testReturnType() {
        Assertions.assertFalse(Types.hasPostConstruct(TC2.class));
    }

    /**
     * @tpTestDetails Test invalid @PostContruct method that throws a checked exception
     * @tpSince RESTEasy 3.7.0
     */
    @Test
    public void testThrowsCheckedException() throws Exception {
        Assertions.assertFalse(Types.hasPostConstruct(TC3.class));
    }

    /**
     * @tpTestDetails Test invalid @PostContruct static method
     * @tpSince RESTEasy 3.7.0
     */
    @Test
    public void testStaticMethod() throws Exception {
        Assertions.assertFalse(Types.hasPostConstruct(TC4.class));
    }

    /**
     * @tpTestDetails Test valid @PostConstruct private method
     * @tpSince RESTEasy 3.7.0
     */
    @Test
    public void testPrivateMethod() throws Exception {
        Assertions.assertTrue(Types.hasPostConstruct(TC5.class));
    }

    /**
     * @tpTestDetails Test valid @PostConstruct inherited method
     * @tpSince RESTEasy 3.7.0
     */
    @Test
    public void testInheritedMethod() throws Exception {
        Assertions.assertTrue(Types.hasPostConstruct(TC6.class));
    }
}
