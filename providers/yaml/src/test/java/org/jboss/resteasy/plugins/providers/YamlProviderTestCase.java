package org.jboss.resteasy.plugins.providers;

import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

public class YamlProviderTestCase {

    @Test
    public void testCreateAllowPatternFromSystemProperty() {
        // set the system property with a class name that should be allowed to be deserialized
        final String className = "org.jboss.resteasy.TestClass";
        String originalPropertyValue = System.setProperty(YamlProvider.ALLOWED_LIST, className);

        try {
            Pattern allowPattern = YamlProvider.createAllowPattern();
            // assert that resulting pattern does contain the *quoted* class name specified in the system property
            Assert.assertTrue("The pattern doesn't contain expected class: " + allowPattern,
                    allowPattern.toString().contains(Pattern.quote(className)));
        } finally {
            // reset the original system property value
            if (originalPropertyValue == null) {
                System.clearProperty(YamlProvider.ALLOWED_LIST);
            } else {
                System.setProperty(YamlProvider.ALLOWED_LIST, originalPropertyValue);
            }
        }
    }
}
