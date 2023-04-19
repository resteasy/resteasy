package org.jboss.resteasy.test.validation.resource;

import javax.validation.constraints.NotEmpty;

public class EmptyArrayValidationFoo {

    @NotEmpty
    Object[] array;

    public EmptyArrayValidationFoo(final Object[] array) {
        this.array = array;
    }

    public EmptyArrayValidationFoo() {
    }

    public Object[] getArray() {
        return array;
    }

    public void setArray(final Object[] array) {
        this.array = array;
    }
}
