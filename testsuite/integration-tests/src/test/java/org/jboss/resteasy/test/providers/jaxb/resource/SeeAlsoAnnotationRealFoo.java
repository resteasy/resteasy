package org.jboss.resteasy.test.providers.jaxb.resource;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SeeAlsoAnnotationRealFoo extends SeeAlsoAnnotationBaseFoo implements SeeAlsoAnnotationFooIntf {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
