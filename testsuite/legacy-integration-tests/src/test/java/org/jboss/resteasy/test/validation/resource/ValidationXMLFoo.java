package org.jboss.resteasy.test.validation.resource;

import java.io.Serializable;

@ValidationXMLFooConstraint(min = 1, max = 3)
public class ValidationXMLFoo implements Serializable {
    public static final long serialVersionUID = -1068336400309384949L;
    public String s;

    public ValidationXMLFoo(final String s) {
        this.s = s;
    }

    public String toString() {
        return "ValidationXMLFoo[" + s + "]";
    }

    public boolean equals(Object o) {
        if (o == null || !(o instanceof ValidationXMLFoo)) {
            return false;
        }
        return this.s.equals(ValidationXMLFoo.class.cast(o).s);
    }

    public int hashCode() {
        return super.hashCode();
    }
}
