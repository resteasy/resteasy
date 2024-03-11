package org.jboss.resteasy.plugins.providers.multipart;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class Mime4JWorkaroundTest {

    private static final Map<String, String> preTestProperties = new HashMap<>();

    @BeforeAll
    public static void saveCurrentStateOfMemThresholdProperty() {
        if (System.getProperties().containsKey(Mime4JWorkaround.MEM_THRESHOLD_PROPERTY)) {
            String value = System.getProperty(Mime4JWorkaround.MEM_THRESHOLD_PROPERTY);
            preTestProperties.put(Mime4JWorkaround.MEM_THRESHOLD_PROPERTY, value);
        }
    }

    @AfterAll
    public static void resetPropertiesToPreTestValues() {
        if (!preTestProperties.containsKey(Mime4JWorkaround.MEM_THRESHOLD_PROPERTY)) {
            System.clearProperty(Mime4JWorkaround.MEM_THRESHOLD_PROPERTY);
        } else {
            preTestProperties.forEach(System::setProperty);
        }
    }

    @BeforeEach
    public void unsetMemThresholdProperty() {
        System.clearProperty(Mime4JWorkaround.MEM_THRESHOLD_PROPERTY);
    }

    @Test
    public void testMemThresholdDefault() {
        assertEquals(Mime4JWorkaround.DEFAULT_MEM_THRESHOLD, Mime4JWorkaround.getMemThreshold());
    }

    @Test
    public void testMemThresholdConfigProperty() {
        System.setProperty(Mime4JWorkaround.MEM_THRESHOLD_PROPERTY, "2048");
        assertEquals(2048, Mime4JWorkaround.getMemThreshold());
    }

    @Test
    public void testInvalidMemThresholdConfigPropertyReturnsDefault_NegativeNumber() {
        System.setProperty(Mime4JWorkaround.MEM_THRESHOLD_PROPERTY, "-2048");
        assertEquals(Mime4JWorkaround.DEFAULT_MEM_THRESHOLD, Mime4JWorkaround.getMemThreshold());
    }

    @Test
    public void testInvalidMemThresholdConfigPropertyReturnsDefault_DecimalNumber() {
        System.setProperty(Mime4JWorkaround.MEM_THRESHOLD_PROPERTY, "2048.2");
        assertEquals(Mime4JWorkaround.DEFAULT_MEM_THRESHOLD, Mime4JWorkaround.getMemThreshold());
    }

    @Test
    public void testInvalidMemThresholdConfigPropertyReturnsDefault_NotANumber() {
        System.setProperty(Mime4JWorkaround.MEM_THRESHOLD_PROPERTY, "Infinity");
        assertEquals(Mime4JWorkaround.DEFAULT_MEM_THRESHOLD, Mime4JWorkaround.getMemThreshold());
    }
}
