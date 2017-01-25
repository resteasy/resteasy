package org.jboss.resteasy.test.client.resource;

public class ClientCustomException extends RuntimeException {

    public ClientCustomException() {
        super("This is a custom Exception");
    }
}
