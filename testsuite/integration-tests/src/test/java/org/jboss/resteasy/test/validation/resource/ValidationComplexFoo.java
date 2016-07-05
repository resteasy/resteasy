package org.jboss.resteasy.test.validation.resource;

import java.io.Serializable;

@ValidationComplexFooConstraint(min = 1, max = 3)
public class ValidationComplexFoo implements Serializable {
    public static final long serialVersionUID = -1068336400309384949L;
    public String s;

    public ValidationComplexFoo(final String s) {
        this.s = s;
    }

    public String toString() {
        return "ValidationComplexFoo[" + s + "]";
    }

    public boolean equals(Object o) {
        if (o == null || !(o instanceof ValidationComplexFoo)) {
            return false;
        }
        return this.s.equals(ValidationComplexFoo.class.cast(o).s);
    }

    public int hashCode() {
        return super.hashCode();
    }
}
