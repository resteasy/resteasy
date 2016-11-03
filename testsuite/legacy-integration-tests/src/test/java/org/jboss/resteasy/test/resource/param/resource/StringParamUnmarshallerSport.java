package org.jboss.resteasy.test.resource.param.resource;

public class StringParamUnmarshallerSport {
    public String name;

    public static StringParamUnmarshallerSport fromString(String str) {
        StringParamUnmarshallerSport s = new StringParamUnmarshallerSport();
        s.name = str;
        return s;
    }


}
