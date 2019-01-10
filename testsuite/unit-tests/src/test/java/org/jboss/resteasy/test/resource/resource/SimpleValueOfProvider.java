package org.jboss.resteasy.test.resource.resource;

public class SimpleValueOfProvider {

    public static String valueOf(String s) {
        throw new RuntimeException("Force error");
    }
}
