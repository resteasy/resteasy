package org.jboss.resteasy.test.providers.jackson2.resource;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlElement;

public class Jackson2XmlResourceWithJacksonAnnotation {
    String attr1;
    String attr2;

    @JsonProperty("attr_1")
    public String getAttr1() {
        return attr1;
    }

    public void setAttr1(String attr1) {
        this.attr1 = attr1;
    }

    @XmlElement
    public String getAttr2() {
        return attr2;
    }

    public void setAttr2(String attr2) {
        this.attr2 = attr2;
    }
}
