package org.jboss.resteasy.plugins.providers.jaxb;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAnyElement;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class JaxbCollection {
    @XmlAnyElement
    private List<Object> value = new ArrayList<Object>();

    public List<Object> getValue() {
        return value;
    }
}
