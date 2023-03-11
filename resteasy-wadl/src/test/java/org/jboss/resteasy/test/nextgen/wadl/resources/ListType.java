package org.jboss.resteasy.test.nextgen.wadl.resources;

import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "listType")
public class ListType {

    private List<String> values;

    @XmlElement
    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }
}
