package org.jboss.resteasy.jose.jws;

public class JWSInputException extends Exception {

    public JWSInputException(final String s) {
        super(s);
    }

    public JWSInputException() {
    }

    public JWSInputException(final Throwable throwable) {
        super(throwable);
    }
}
