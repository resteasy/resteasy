package org.jboss.resteasy.test.resource.resource;

public class SimpleFromStringProvider {

    public static String fromString(String s) {
        throw new RuntimeException("Force error");
    }
}
