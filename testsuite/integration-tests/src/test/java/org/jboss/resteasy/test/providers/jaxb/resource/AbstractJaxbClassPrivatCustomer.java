package org.jboss.resteasy.test.providers.jaxb.resource;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public final class AbstractJaxbClassPrivatCustomer extends AbstractJaxbClassCustomer {
    private static final long serialVersionUID = 133152931415808605L;

    @Override
    public String getArt() {
        return "PRIVATCUSTOMER";
    }

    @Override
    public String toString() {
        return "{" + super.toString() + ", familienstand=nada'}'";
    }
}
