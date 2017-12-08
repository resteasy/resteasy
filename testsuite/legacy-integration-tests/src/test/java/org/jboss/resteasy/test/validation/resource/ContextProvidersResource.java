package org.jboss.resteasy.test.validation.resource;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.annotations.providers.multipart.PartType;
import org.jboss.resteasy.annotations.providers.multipart.XopWithMultipartRelated;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataOutput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartInput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartOutput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartRelatedInput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartRelatedOutput;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Path("")
public class ContextProvidersResource {
    @GET
    @Produces("multipart/mixed")
    @Path("get/mixed")
    public MultipartOutput getMixed() {
        MultipartOutput output = new MultipartOutput();
        output.addPart(new ContextProvidersCustomer("Bill"), MediaType.APPLICATION_XML_TYPE);
        output.addPart("Bob", MediaType.TEXT_PLAIN_TYPE);
        return output;
    }

    @GET
    @Produces("multipart/form-data")
    @MultipartForm
    @Path("get/form")
    public MultipartFormDataOutput getForm() {
        MultipartFormDataOutput output = new MultipartFormDataOutput();
        output.addFormData("bill", new ContextProvidersCustomer("Bill-2"), MediaType.APPLICATION_XML_TYPE, "tmp2");
        output.addFormData("bob", "Bob", MediaType.TEXT_PLAIN_TYPE);
        output.addFormData("bill", new ContextProvidersCustomer("Bill"), MediaType.APPLICATION_XML_TYPE, "tmp1");
        return output;
    }

    @GET
    @Produces("multipart/mixed")
    @PartType("application/xml")
    @Path("get/list")
    public List<ContextProvidersCustomer> getList() {
        List<ContextProvidersCustomer> list = new ArrayList<ContextProvidersCustomer>();
        list.add(new ContextProvidersCustomer("Bill"));
        list.add(new ContextProvidersCustomer("Bob"));
        return list;
    }

    @GET
    @Produces("multipart/form-data")
    @PartType("application/xml")
    @Path("get/map")
    public Map<String, ContextProvidersCustomer> getMap() {
        Map<String, ContextProvidersCustomer> map = new HashMap<String, ContextProvidersCustomer>();
        map.put("bill", new ContextProvidersCustomer("Bill"));
        map.put("bob", new ContextProvidersCustomer("Bob"));
        return map;
    }

    @GET
    @Produces("multipart/related")
    @Path("get/related")
    public MultipartRelatedOutput getRelated() {
        MultipartRelatedOutput output = new MultipartRelatedOutput();
        output.setStartInfo("text/html");
        output.addPart("Bill", new MediaType("image", "png"), "bill", "binary");
        output.addPart("Bob", new MediaType("image", "png"), "bob", "binary");
        return output;
    }

    @GET
    @Path("get/multipartform")
    @Produces("multipart/form-data")
    @MultipartForm
    public ContextProvidersCustomerForm getMultipartForm() {
        ContextProvidersCustomerForm form = new ContextProvidersCustomerForm();
        form.setCustomer(new ContextProvidersCustomer("Bill"));
        return form;
    }

    @GET
    @Path("get/xop")
    @Produces("multipart/related")
    @XopWithMultipartRelated
    public ContextProvidersXop getXop() {
        return new ContextProvidersXop("goodbye world".getBytes());
    }

    @POST
    @Consumes("multipart/mixed")
    @Produces(MediaType.APPLICATION_XML)
    @Path("post/mixed")
    public List<ContextProvidersName> postMixed(MultipartInput input) throws IOException {
        List<InputPart> list = input.getParts();
        List<ContextProvidersName> names = new ArrayList<ContextProvidersName>();
        for (Iterator<InputPart> it = list.iterator(); it.hasNext(); ) {
            InputPart inputPart = it.next();
            if (MediaType.APPLICATION_XML_TYPE.equals(inputPart.getMediaType())) {
                names.add(new ContextProvidersName(inputPart.getBody(ContextProvidersCustomer.class, null).getName()));
            } else {
                names.add(new ContextProvidersName(inputPart.getBody(String.class, null)));
            }
        }
        return names;
    }

    @POST
    @Consumes("multipart/form-data")
    @Produces(MediaType.APPLICATION_XML)
    @Path("post/form")
    public List<ContextProvidersName> postForm(MultipartFormDataInput input) throws IOException {
        Map<String, List<InputPart>> map = input.getFormDataMap();
        List<ContextProvidersName> names = new ArrayList<ContextProvidersName>();
        for (Iterator<String> it = map.keySet().iterator(); it.hasNext(); ) {
            String key = it.next();
            InputPart inputPart = map.get(key).iterator().next();
            if (MediaType.APPLICATION_XML_TYPE.equals(inputPart.getMediaType())) {
                names.add(new ContextProvidersName(inputPart.getBody(ContextProvidersCustomer.class, null).getName()));
            } else {
                names.add(new ContextProvidersName(inputPart.getBody(String.class, null)));
            }
        }
        return names;
    }

    @POST
    @Consumes("multipart/mixed")
    @Produces(MediaType.APPLICATION_XML)
    @Path("post/list")
    public List<ContextProvidersName> postList(List<ContextProvidersCustomer> customers) throws IOException {
        List<ContextProvidersName> names = new ArrayList<ContextProvidersName>();
        for (ContextProvidersCustomer customer : customers) {
            names.add(new ContextProvidersName(customer.getName()));
        }
        return names;
    }

    @POST
    @Consumes("multipart/form-data")
    @Produces(MediaType.APPLICATION_XML)
    @Path("post/map")
    public List<ContextProvidersName> postMap(Map<String, ContextProvidersCustomer> customers) throws IOException {
        List<ContextProvidersName> names = new ArrayList<ContextProvidersName>();
        for (Iterator<String> it = customers.keySet().iterator(); it.hasNext(); ) {
            String key = it.next();
            ContextProvidersCustomer customer = customers.get(key);
            names.add(new ContextProvidersName(key + ":" + customer.getName()));
        }
        return names;
    }

    @POST
    @Consumes("multipart/related")
    @Produces(MediaType.APPLICATION_XML)
    @Path("post/related")
    public List<ContextProvidersName> postRelated(MultipartRelatedInput customers) throws IOException {
        List<ContextProvidersName> names = new ArrayList<ContextProvidersName>();
        for (Iterator<InputPart> it = customers.getParts().iterator(); it.hasNext(); ) {
            InputPart part = it.next();
            String name = part.getBody(String.class, null);
            names.add(new ContextProvidersName(name));
        }
        return names;
    }

    @POST
    @Consumes("multipart/form-data")
    @Path("post/multipartform")
    public String postMultipartForm(@MultipartForm ContextProvidersCustomerForm form) throws IOException {
        return form.getCustomer().getName();
    }

    @POST
    @Path("post/xop")
    @Consumes("multipart/related")
    public String postXop(@XopWithMultipartRelated ContextProvidersXop xop) {
        return new String(xop.getBytes());
    }
}
