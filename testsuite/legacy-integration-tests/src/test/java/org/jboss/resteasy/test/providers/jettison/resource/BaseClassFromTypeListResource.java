package org.jboss.resteasy.test.providers.jettison.resource;

import org.junit.Assert;

import javax.ws.rs.Path;
import java.util.ArrayList;
import java.util.List;

@Path("/")
public class BaseClassFromTypeListResource implements BaseClassFromTypeListInAccountsIntf {
    public List<BaseClassFromTypeListCustomer> list() {
        ArrayList<BaseClassFromTypeListCustomer> set = new ArrayList<BaseClassFromTypeListCustomer>();
        set.add(new BaseClassFromTypeListCustomer("bill"));
        set.add(new BaseClassFromTypeListCustomer("monica"));

        return set;
    }

    public void put(List<BaseClassFromTypeListCustomer> customers) {
        Assert.assertEquals("bill", customers.get(0).getName());
        Assert.assertEquals("monica", customers.get(1).getName());
    }
}
