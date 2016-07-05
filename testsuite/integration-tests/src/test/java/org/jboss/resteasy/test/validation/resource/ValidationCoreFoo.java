package org.jboss.resteasy.test.validation.resource;

import java.io.Serializable;

@ValidationCoreFooConstraint(min = 1, max = 3)
public class ValidationCoreFoo implements Serializable {
    private static final long serialVersionUID = -1068336400309384949L;
    public String s;

    public ValidationCoreFoo(final String s) {
        this.s = s;
    }

    public String toString() {
        return "ValidationCoreFoo[" + s + "]";
    }

    public boolean equals(Object o) {
        if (o == null || !(o instanceof ValidationCoreFoo)) {
            return false;
        }
        return this.s.equals(ValidationCoreFoo.class.cast(o).s);
    }

    public int hashCode() {
        return super.hashCode();
    }
}

