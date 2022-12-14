package org.jboss.resteasy.test.providers.jackson2.resource;

public class ExceptionMapperMarshalErrorMessage {
    String error;

    public ExceptionMapperMarshalErrorMessage(final String error) {
        this.error = error;
    }

    public ExceptionMapperMarshalErrorMessage() {
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
