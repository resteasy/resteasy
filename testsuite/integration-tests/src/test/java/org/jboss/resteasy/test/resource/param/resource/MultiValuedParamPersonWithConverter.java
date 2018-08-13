package org.jboss.resteasy.test.resource.param.resource;

import java.util.Objects;

public class MultiValuedParamPersonWithConverter implements Comparable<MultiValuedParamPersonWithConverter> {

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
        MultiValuedParamPersonWithConverter person = (MultiValuedParamPersonWithConverter) o;
        return Objects.equals(name, person.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name);
    }

    @Override
    public int compareTo(MultiValuedParamPersonWithConverter o) {
        return this.toString().compareTo(o.toString());
    }
}
