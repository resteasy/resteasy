package org.jboss.resteasy.test.resource.param.resource;

import javax.ws.rs.ext.ParamConverter;
import java.util.Collection;
import java.util.HashSet;
import java.util.TreeSet;

public class PersonParamSetConverter implements ParamConverter<Collection<?>> {

    @Override
    public Collection<?> fromString(String param) {
        if (param == null || param.trim().isEmpty()) {
            return null;
        }
        return parse(param.split(","));
    }

    @Override
    public String toString(Collection<?> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        return stringify(list);
    }

    private Collection<PersonWithConverter> parse(String[] params) {
        Collection<PersonWithConverter> list = new HashSet<PersonWithConverter>();
        for (String param : params) {
            PersonWithConverter person = new PersonWithConverter();
            person.setName(param);
            list.add(person);
        }
        return list;
    }

    private <T> String stringify(Collection<T> list) {
        StringBuffer sb = new StringBuffer();
        for (T s : list) {
            sb.append(s.toString()).append(',');
        }
        return sb.toString();
    }
}