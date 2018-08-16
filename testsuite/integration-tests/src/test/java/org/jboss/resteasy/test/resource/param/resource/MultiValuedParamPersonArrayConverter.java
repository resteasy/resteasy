package org.jboss.resteasy.test.resource.param.resource;

import javax.ws.rs.ext.ParamConverter;

public class MultiValuedParamPersonArrayConverter implements ParamConverter<MultiValuedParamPersonWithConverter[]> {

    @Override
    public MultiValuedParamPersonWithConverter[] fromString(String param) {
        if (param == null || param.trim().isEmpty()) {
            return null;
        }
        // cookies doesn't allow to use ',', see the spec (https://tools.ietf.org/html/rfc6265), so we need to use also '-'
        String[] names = param.split("[,-]");
        MultiValuedParamPersonWithConverter[] people = new MultiValuedParamPersonWithConverter[names.length];
        int i = 0;
        for (String name : names) {
            MultiValuedParamPersonWithConverter person = new MultiValuedParamPersonWithConverter();
            person.setName(name);
            people[i++] = person;
        }
        return people;
    }

    @Override
    public String toString(MultiValuedParamPersonWithConverter[] array) {
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