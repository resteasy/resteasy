package org.jboss.resteasy.test.resource.param.resource;

import java.util.Objects;

public class PersonWithValueOf implements Comparable<PersonWithValueOf> {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Hello I am " + name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonWithValueOf personWithValueOf = (PersonWithValueOf) o;
        return Objects.equals(name, personWithValueOf.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name);
    }

    @Override
    public int compareTo(PersonWithValueOf o) {
        return this.toString().compareTo(o.toString());
    }

    public static PersonWithValueOf valueOf(String name) {
        PersonWithValueOf personWithValueOf = new PersonWithValueOf();
        personWithValueOf.setName(name);
        return personWithValueOf;
    }
}
