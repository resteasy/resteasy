package org.jboss.resteasy.test.providers.jackson2.resource;

public class ExceptionMapperMarshalMyCustomException extends RuntimeException {
    public ExceptionMapperMarshalMyCustomException(final String message) {
        super(message);
    }
}
