package org.jboss.resteasy.resteasy1125;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * RESTEASY-1125
 *
 * Nov 19, 2014
 */
@XmlRootElement
public class Model {
    private String _s;
    public String getS() {
        return _s;
    }
    public void setS(String s) {
        _s = s;
    }
}