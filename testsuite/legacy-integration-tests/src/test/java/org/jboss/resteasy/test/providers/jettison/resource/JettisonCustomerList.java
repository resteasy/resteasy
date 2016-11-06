package org.jboss.resteasy.test.providers.jettison.resource;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Collection;

@XmlRootElement(name = "customers")
public class JettisonCustomerList {

    @XmlElementRef
    public Collection<JettisonCustomer> customers;

    public JettisonCustomerList() {
    }

    public JettisonCustomerList(Collection<JettisonCustomer> customers) {
        this.customers = customers;
    }

    public int size() {
        return customers.size();
    }
}
