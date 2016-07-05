package org.jboss.resteasy.test.resource.param.resource;

import java.io.Serializable;

public class SerializableWithParametersObject implements Serializable {
    private static final long serialVersionUID = -1068336400309384949L;
    private String s;

    public SerializableWithParametersObject(final String s) {
        this.s = s;
    }

    public String toString() {
        return "Foo[" + s + "]";
    }

    public boolean equals(Object o) {
        if (o == null || !(o instanceof SerializableWithParametersObject)) {
            return false;
        }
        return this.s.equals(SerializableWithParametersObject.class.cast(o).s);
    }

    public int hashCode() {
        return super.hashCode();
    }
}
