package org.jboss.resteasy.test.interceptor.resource;

public class CustomException extends RuntimeException {

    public CustomException() {
        super("This is a custom Exception");
    }
}
