package org.jboss.resteasy.test.providers.jaxb.resource;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

@XmlEnum
public enum XmlEnumParamLocation {

    @XmlEnumValue("north")
    NORTH("north"),
    @XmlEnumValue("south")
    SOUTH("south"),
    @XmlEnumValue("east")
    EAST("east"),
    @XmlEnumValue("west")
    WEST("west");
    private final String value;

    XmlEnumParamLocation(final String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static XmlEnumParamLocation fromValue(String v) {
        for (XmlEnumParamLocation c : XmlEnumParamLocation.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v.toString());
    }

}
