package org.jboss.resteasy.test.exception.resource;

public class ExceptionMapperMyCustomException extends RuntimeException {
    public ExceptionMapperMyCustomException(final String message) {
        super(message);
    }
}
