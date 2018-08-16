package org.jboss.resteasy.test.resource.param.resource;

import javax.ws.rs.ext.ParamConverter;
import java.util.ArrayList;
import java.util.Collection;

public class MultiValuedParamPersonListConverter implements ParamConverter<Collection<?>> {

    @Override
    public Collection<?> fromString(String param) {
        if (param == null || param.trim().isEmpty()) {
            return null;
        }
        // cookies doesn't allow to use ',', see the spec (https://tools.ietf.org/html/rfc6265), so we need to use also '-'
        return parse(param.split("[,-]"));
    }

    @Override
    public String toString(Collection<?> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        return stringify(list);
    }

    private Collection<MultiValuedParamPersonWithConverter> parse(String[] params) {
        Collection<MultiValuedParamPersonWithConverter> list = new ArrayList<MultiValuedParamPersonWithConverter>();
        for (String param : params) {
            MultiValuedParamPersonWithConverter person = new MultiValuedParamPersonWithConverter();
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