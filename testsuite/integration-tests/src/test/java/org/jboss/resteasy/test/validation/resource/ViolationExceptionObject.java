package org.jboss.resteasy.test.validation.resource;

import java.io.Serializable;

@ViolationExceptionConstraint(min = 1, max = 3)
public class ViolationExceptionObject implements Serializable {
    public static final long serialVersionUID = -1068336400309384949L;
    public String s;

    public ViolationExceptionObject(final String s) {
        this.s = s;
    }

    public String toString() {
        return "Foo[" + s + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof ViolationExceptionObject)) {
            return false;
        }
        return this.s.equals(ViolationExceptionObject.class.cast(o).s);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
