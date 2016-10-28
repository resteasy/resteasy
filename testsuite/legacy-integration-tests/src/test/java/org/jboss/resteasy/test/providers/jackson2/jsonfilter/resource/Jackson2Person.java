package org.jboss.resteasy.test.providers.jackson2.jsonfilter.resource;


import com.fasterxml.jackson.annotation.JsonFilter;

@JsonFilter(value="nameFilterOutAllExcept")
public class Jackson2Person {
    private String name;
    private int id;
    private String address;
    private PersonType personType;

    public Jackson2Person() {
    }

    public Jackson2Person(String name, int id, String address, PersonType personType) {
        this.name = name;
        this.id = id;
        this.address = address;
        this.personType = personType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public PersonType getPersonType() {
        return personType;
    }

    public void setPersonType(PersonType personType) {
        this.personType = personType;
    }
}
