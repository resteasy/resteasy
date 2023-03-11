package org.jboss.resteasy.test;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@XmlRootElement(name = "data")
public class XmlData {
    protected String name;

    public XmlData(final String data) {
        this.name = data;
    }

    public XmlData() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
