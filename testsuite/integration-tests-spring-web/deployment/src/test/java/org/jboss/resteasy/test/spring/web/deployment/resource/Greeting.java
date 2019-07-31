package org.jboss.resteasy.test.spring.web.deployment.resource;

public class Greeting {

    private final String message;

    public Greeting(final String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
