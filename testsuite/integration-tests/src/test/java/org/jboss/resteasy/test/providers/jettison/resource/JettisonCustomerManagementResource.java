package org.jboss.resteasy.test.providers.jettison.resource;

import org.jboss.resteasy.annotations.providers.jaxb.json.Mapped;
import org.jboss.resteasy.annotations.providers.jaxb.json.XmlNsMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Path("/management")
@Produces(MediaType.APPLICATION_XML)
@Consumes(MediaType.APPLICATION_XML)
public class JettisonCustomerManagementResource {

    @GET
    @Path("/customers")
    @Produces(MediaType.APPLICATION_JSON)
    @Mapped(namespaceMap = {
            @XmlNsMap(namespace = "http://namespace.org/customermanagement", jsonName = "cusotmers")
    })
    public JettisonCustomerList findCutomerJSON() {

        int capacity = 4;
        final List<JettisonCustomer> customers = new ArrayList<>(capacity);
        for (int i = 0; i < capacity; i++) {
            final JettisonCustomer cust = new JettisonCustomer();
            cust.setId(Long.valueOf(i));
            cust.setSurname("Lastname" + i);
            cust.setSince(new Date());

            customers.add(cust);
        }

        final JettisonCustomerList customerList = new JettisonCustomerList(customers);
        return customerList;
    }
}
