package org.jboss.resteasy.test.resource.param.resource;

import javax.ws.rs.ext.ParamConverter;

public class ParamConverterPOJOConverter implements ParamConverter<ParamConverterPOJO> {
    public ParamConverterPOJO fromString(String str) {
        ParamConverterPOJO pojo = new ParamConverterPOJO();
        pojo.setName(str);
        return pojo;
    }

    public String toString(ParamConverterPOJO value) {
        return value.getName();
    }
}
