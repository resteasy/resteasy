package org.jboss.resteasy.test.xxe.resource;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SecureProcessingBar {
    private String _s;

    public String getS() {
        return _s;
    }

    public void setS(String s) {
        _s = s;
    }
}
