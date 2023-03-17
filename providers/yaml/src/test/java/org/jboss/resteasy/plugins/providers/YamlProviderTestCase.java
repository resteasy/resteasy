package org.jboss.resteasy.plugins.providers;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings("deprecation")
public class YamlProviderTestCase {

    @Test
    public void testCreateAllowPatternFromSystemProperty() {
        // Set the system property with a class name that should be allowed to be deserialized
        final String className = "org.jboss.resteasy.TestClass";
        String originalPropertyValue = System.setProperty(YamlProvider.ALLOWED_LIST, className);

        try {
            List<String> allowList = YamlProvider.createAllowList();
            // Assert that resulting pattern does contain the class name specified in the system property
            Assert.assertTrue("The allowed classes list doesn't contain expected class " + className,
                    allowList.contains(className));
        } finally {
            // Reset the original system property value
            if (originalPropertyValue == null) {
                System.clearProperty(YamlProvider.ALLOWED_LIST);
            } else {
                System.setProperty(YamlProvider.ALLOWED_LIST, originalPropertyValue);
            }
        }
    }
}
