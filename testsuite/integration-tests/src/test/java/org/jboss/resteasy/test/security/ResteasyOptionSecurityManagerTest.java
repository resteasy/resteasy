package org.jboss.resteasy.test.security;

import java.lang.reflect.Method;
import java.security.Permission;
import java.util.Collections;
import java.util.Set;

import org.jboss.resteasy.core.ResteasyDeploymentImpl;
import org.jboss.resteasy.spi.ResteasyConfiguration;
import org.jboss.resteasy.spi.config.Options;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test for RESTEASY-3593: Pass the ResteasyConfiguration to the Option.getValue() when security manager is enabled
 *
 * This test focuses specifically on the bug fix in ResteasyDeploymentImpl.getOptionValue():
 * - Before fix: AccessController.doPrivileged((PrivilegedAction<T>) option::getValue);
 * - After fix: AccessController.doPrivileged((PrivilegedAction<T>) () -> option.getValue(config));
 */
public class ResteasyOptionSecurityManagerTest {

    private static SecurityManager originalSecurityManager;
    private static final String TEST_OPTION_KEY = "org.jboss.resteasy.test.security.option";
    private static final String TEST_OPTION_VALUE = "test-value";

    @BeforeAll
    public static void checkSecurityManagerSupport() {
        // SecurityManager was deprecated in JDK 17 and removed in JDK 21
        int javaVersion = getJavaVersion();
        Assumptions.assumeTrue(javaVersion < 21,
                "SecurityManager is not supported in JDK 21+. This test requires JDK 8-20.");
    }

    private static int getJavaVersion() {
        String version = System.getProperty("java.version");
        if (version.startsWith("1.")) {
            // Java 8 or earlier
            return Integer.parseInt(version.substring(2, 3));
        } else {
            // Java 9 or later
            return Integer.parseInt(version.split("\\.")[0]);
        }
    }

    /**
     * Custom Options class for testing
     */
    private static class TestOptions {
        // Store the config parameter that was passed to getValue method
        static ResteasyConfiguration passedConfig = null;

        // Create a test option that tracks the config parameter passed to it
        static final Options<String> TEST_OPTION = new Options<String>(TEST_OPTION_KEY, String.class, () -> "default-value") {
            @Override
            public String getValue(ResteasyConfiguration configuration) {
                passedConfig = configuration;
                return super.getValue(configuration);
            }
        };
    }

    @BeforeEach
    public void setup() {
        // Reset the tracked config
        TestOptions.passedConfig = null;

        // Save the original security manager
        originalSecurityManager = System.getSecurityManager();

        // Set the test system property
        System.setProperty(TEST_OPTION_KEY, TEST_OPTION_VALUE);
    }

    @AfterEach
    public void teardown() {
        // Restore original security manager
        System.setSecurityManager(originalSecurityManager);
        System.clearProperty(TEST_OPTION_KEY);
    }

    /**
     * Test the bug fix directly by calling the fixed getOptionValue method via reflection
     * This method tests that:
     * 1. The config parameter is correctly passed to the option.getValue method
     * 2. The fix uses a lambda that calls option.getValue(config) instead of option::getValue
     */
    @Test
    public void testOptionGetValueWithSecurityManager() throws Exception {
        // Set a test security manager for this test that allows all permissions
        // We just need the security manager to be active, but we don't want it to
        // block any operations needed by the test framework
        System.setSecurityManager(new SecurityManager() {
            @Override
            public void checkPermission(Permission perm) {
                // Allow all permissions, we just need the SecurityManager to be active
                // The important thing is that System.getSecurityManager() != null
                // so that the code under test uses AccessController.doPrivileged()
            }

            @Override
            public void checkExit(int status) {
                // Allow exit without throwing SecurityException
            }
        });

        // Verify security manager is active
        Assertions.assertNotNull(System.getSecurityManager(),
                "Security manager must be active for this test");

        // Get access to the private getOptionValue method
        Method getOptionValueMethod = ResteasyDeploymentImpl.class.getDeclaredMethod(
                "getOptionValue", Options.class, ResteasyConfiguration.class);
        getOptionValueMethod.setAccessible(true);

        // Create a mock configuration
        ResteasyConfiguration mockConfig = createMockConfig();

        // Call the getOptionValue method with our test option and config
        String result = (String) getOptionValueMethod.invoke(null, TestOptions.TEST_OPTION, mockConfig);

        // Verify the result
        Assertions.assertEquals(TEST_OPTION_VALUE, result, "Should return value from the configuration");

        // Verify that the config was passed to the getValue method (this is the key part of the fix)
        Assertions.assertSame(mockConfig, TestOptions.passedConfig,
                "Config should be passed to option.getValue when security manager is enabled");
    }

    /**
     * Test the behavior with security manager disabled to ensure it works in both modes
     */
    @Test
    public void testOptionGetValueWithoutSecurityManager() throws Exception {
        // Ensure security manager is disabled
        System.setSecurityManager(null);
        Assertions.assertNull(System.getSecurityManager(), "Security manager should be null for this test");

        // Get access to the private getOptionValue method
        Method getOptionValueMethod = ResteasyDeploymentImpl.class.getDeclaredMethod(
                "getOptionValue", Options.class, ResteasyConfiguration.class);
        getOptionValueMethod.setAccessible(true);

        // Create a mock configuration
        ResteasyConfiguration mockConfig = createMockConfig();

        // Call the getOptionValue method with our test option and config
        String result = (String) getOptionValueMethod.invoke(null, TestOptions.TEST_OPTION, mockConfig);

        // Verify the result
        Assertions.assertEquals(TEST_OPTION_VALUE, result, "Should return value from the configuration");

        // Verify that the config was passed (with no security manager, it should be passed directly)
        Assertions.assertSame(mockConfig, TestOptions.passedConfig,
                "Config should be passed to option.getValue even when security manager is disabled");
    }

    /**
     * Helper method to create a mock ResteasyConfiguration
     */
    private ResteasyConfiguration createMockConfig() {
        return new ResteasyConfiguration() {
            @Override
            public String getParameter(String name) {
                if (TEST_OPTION_KEY.equals(name)) {
                    return TEST_OPTION_VALUE;
                }
                return null;
            }

            @Override
            public Set<String> getParameterNames() {
                return Collections.singleton(TEST_OPTION_KEY);
            }

            @Override
            public String getInitParameter(String name) {
                return null; // No init parameters in our test
            }

            @Override
            public Set<String> getInitParameterNames() {
                return Collections.emptySet(); // No init parameters in our test
            }
        };
    }
}
