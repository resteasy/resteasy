package org.jboss.resteasy.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assume;

/**
 * @author <a href="mailto:jperkins@redhat.com">James R. Perkins</a>
 */
public class AssumeUtils {

    private static final int JAVA_VERSION;
    private static final boolean ELYTRON;

    static {
        final String version = System.getProperty("java.specification.version");
        final Matcher matcher = Pattern.compile("^(?:1\\.)?(\\d+)$").matcher(version);
        if (matcher.find()) {
            JAVA_VERSION = Integer.parseInt(matcher.group(1));
        } else {
            JAVA_VERSION = 8;
        }
        ELYTRON = Boolean.getBoolean("elytron");
    }

    /**
     * Checks if the JVM is Java 14 or higher or Elytron is enabled. PicketBox tests only work on JVM's lower than 14.
     */
    public static void checkElytronEnabled() {
        Assume.assumeTrue(String.format("Running on Java SE %d only allowed with Elytron.", JAVA_VERSION),
                ELYTRON || JAVA_VERSION < 14);
    }

    /**
     * Checks if the JVM is Java 14. PicketBox tests only work on JVM's lower than 14.
     */
    public static void checkPicketBox() {
        Assume.assumeTrue(String.format("Running on Java SE %d is not supported for PicketBox base tests.", JAVA_VERSION),
                JAVA_VERSION < 14);
    }
}
