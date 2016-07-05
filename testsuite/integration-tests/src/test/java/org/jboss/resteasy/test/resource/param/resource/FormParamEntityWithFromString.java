package org.jboss.resteasy.test.resource.param.resource;

public class FormParamEntityWithFromString extends FormParamEntityPrototype implements
        Comparable<FormParamEntityWithFromString> {

    public static FormParamEntityWithFromString fromString(String arg) {
        FormParamEntityWithFromString newEntity = new FormParamEntityWithFromString();
        newEntity.value = arg;
        return newEntity;
    }

    @Override
    public int compareTo(FormParamEntityWithFromString o) {
        return this.value.compareTo(o.value);
    }

    @Override
    public boolean equals(Object obj) {
        return this.value.equals(obj);
    }

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }
}
