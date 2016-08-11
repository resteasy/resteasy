package org.jboss.resteasy.test.exception.resource;

public class ExceptionMapperMyCustomSubException extends ExceptionMapperMyCustomException {

   private static final long serialVersionUID = 1L;

   public ExceptionMapperMyCustomSubException(final String message) {
        super(message);
    }
}
