package org.jboss.resteasy.test.validation.resource;

import java.io.Serializable;

@ValidationFooConstraint(min = 1, max = 3)
public class ValidationFoo implements Serializable {
    private static final long serialVersionUID = -1068336400309384949L;
    public String s;

    public ValidationFoo(final String s) {
        this.s = s;
    }

    public String toString() {
        return "ValidationFoo[" + s + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof ValidationFoo)) {
            return false;
        }
        return this.s.equals(ValidationFoo.class.cast(o).s);
    }

    @Override
    public int hashCode() {
        return s.hashCode();
    }
}

