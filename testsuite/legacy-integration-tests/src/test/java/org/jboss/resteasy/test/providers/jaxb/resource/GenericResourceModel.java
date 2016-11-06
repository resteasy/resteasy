package org.jboss.resteasy.test.providers.jaxb.resource;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GenericResourceModel {
    private String _s;

    public String getS() {
        return _s;
    }

    public void setS(String s) {
        _s = s;
    }
}
