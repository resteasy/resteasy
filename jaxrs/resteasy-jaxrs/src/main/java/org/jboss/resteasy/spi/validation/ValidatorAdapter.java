package org.jboss.resteasy.spi.validation;

import java.lang.reflect.Method;

public interface ValidatorAdapter {

	void applyValidation(Object resource, Method invokedMethod, Object[] args);

}
