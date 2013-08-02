package org.jboss.resteasy.jsapi.testing;

import org.jboss.resteasy.annotations.Form;
import org.jboss.resteasy.jsapi.testing.form.*;

import javax.ws.rs.*;

/**
 * @author Weinan Li
 * @created_at 08 24 2012
 */
@Path("/smoke")
public class SmokeTestResource {
    @Path("{id}")
    @GET
    @Produces("text/plain")
    public String testPathParam(@PathParam("id") String id) {
        return id;
    }

    @POST
    @Produces("text/plain")
    public String testFormParam(@FormParam("key") String[] values) {
        String val = "";
        for (String _val : values) {
            val += _val + "::";
        }
        return val;
    }

    @Path("/post2")
    @POST
    @Produces("text/plain")
    public String testFormParam2(@FormParam("key") String val) {
        return val;
    }

    @GET
    @Produces("text/plain")
    public String testQueryParam(@QueryParam("key") String[] values) {
        String val = "";
        for (String _val : values) {
            val += _val + "::";
        }
        return val;
    }

    @Path("/cookie")
    @GET
    @Produces("text/plain")
    public String testCookieParam(@CookieParam("username") String key) {
        return key;
    }

    @GET
    @Path("/matrix")
    @Produces("text/plain")
    public String testMatrixParam(@MatrixParam("key") String[] key) {
        String val = "";
        for (String _val : key) {
            val += _val + "::";
        }
        return val;
    }

    @GET
    @Path("/header")
    @Produces("text/plain")
    public String testHeaderParam(@HeaderParam("Referer") String referer) {
        return referer;
    }

    @POST
    @Path("/RESTEASY-731/false")
    @Produces("text/plain")
    public String testRESTEasy731False(@FormParam("false") boolean bool) {
        return ("RESTEASY-731-" + String.valueOf(bool));
    }

    @POST
    @Path("/RESTEASY-731/zero")
    @Produces("text/plain")
    public String testRESTEasy731Zero(@FormParam("zero") int zero) {
        return ("RESTEASY-731-" + String.valueOf(zero));
    }

    @POST
    @Path("/RESTEASY-805/form1")
    @Produces("text/plain")
    public String testRESTEasy805(@Form MyForm myForm) {
        StringBuilder ret = new StringBuilder();
        for (String key : myForm.getMyMap().keySet()) {
            ret.append(myForm.getMyMap().get(key).getBar());
        }
        return ret.toString();
    }

    @POST
    @Path("/RESTEASY-805/form2")
    @Produces("text/plain")
    public String testRESTEasy805Case2(@Form MyForm2 myForm2) {
        return myForm2.getHeader() + myForm2.getStuff() + myForm2.getNumber();
    }

    @POST
    @Path("/RESTEASY-805/form3")
    @Produces("text/plain")
    public String testRESTEasy805Case3(@Form MyForm3 myForm3) {
        StringBuilder ret = new StringBuilder();
        for (Foo foo : myForm3.getFoos()) {
            ret.append(foo.getBar());
        }
        return ret.toString();
    }

    @POST
    @Path("/postPrefixForm")
    @Produces("text/plain")
    public String postPrefixForm(@Form Person person) {
        StringBuilder ret = new StringBuilder();
        for (TelephoneNumber number : person.getTelephoneNumbers()) {
            ret.append(number.getNumber()).append(number.getCountryCode());
        }

        for (String key : person.getAddresses().keySet()) {
            Address address = person.getAddresses().get(key);
            ret.append(address.getHouseNumber()).append(address.getStreet());
        }
        return ret.toString();
    }
}
