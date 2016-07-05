package org.jboss.resteasy.test.providers.jaxb.resource;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "test-data")
public class CharacterSetData {
    private String text = "Text \u0100.";

    public String getText() {
        return text;
    }

    public void setText(String value) {
        text = value;
    }
}
