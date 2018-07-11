package org.jboss.resteasy.test.resource.param.resource;

import java.util.Objects;

public class PersonWithFromString implements Comparable<PersonWithFromString> {

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
        PersonWithFromString personWithFromString = (PersonWithFromString) o;
        return Objects.equals(name, personWithFromString.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name);
    }

    @Override
    public int compareTo(PersonWithFromString o) {
        return this.toString().compareTo(o.toString());
    }

    public static PersonWithFromString fromString(String name) {
        PersonWithFromString personWithFromString = new PersonWithFromString();
        personWithFromString.setName(name);
        return personWithFromString;
    }
}
