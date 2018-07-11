package org.jboss.resteasy.test.resource.param.resource;

import javax.ws.rs.ext.ParamConverter;

public class PersonParamArrayConverter implements ParamConverter<PersonWithConverter[]> {

    @Override
    public PersonWithConverter[] fromString(String param) {
        if (param == null || param.trim().isEmpty()) {
            return null;
        }
        String[] names = param.split(",");
        PersonWithConverter[] people = new PersonWithConverter[names.length];
        int i = 0;
        for (String name : names) {
            PersonWithConverter person = new PersonWithConverter();
            person.setName(name);
            people[i++] = person;
        }
        return people;
    }

    @Override
    public String toString(PersonWithConverter[] array) {
        if (array == null || array.length == 0) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < array.length; i++) {
            sb.append(array[i].toString()).append(",");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }
}