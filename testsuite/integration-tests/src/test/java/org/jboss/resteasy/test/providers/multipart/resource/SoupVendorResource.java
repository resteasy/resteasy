package org.jboss.resteasy.test.providers.multipart.resource;

import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartInput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartOutput;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Path("vendor")
public class SoupVendorResource {

    public static List<String> getControlList() {
        ArrayList<String> controlList = new ArrayList<>();
        controlList.add("Chicken Noodle");
        controlList.add("Vegetable");
        controlList.add("Granny's Soups");
        return controlList;
    }

    private MultipartOutput createMsg() {
        MultipartOutput multipartOutput = new MultipartOutput();

        multipartOutput.addPart(new Soup("Chicken Noodle"),
                MediaType.APPLICATION_XML_TYPE);
        multipartOutput.addPart(new Soup("Vegetable"),
                MediaType.APPLICATION_XML_TYPE);
        multipartOutput.addPart("Granny's Soups", MediaType.TEXT_PLAIN_TYPE);

        return multipartOutput;
    }

    @GET
    @Path("soups/obj")
    @Produces("multipart/mixed")
    public MultipartOutput soupsObj() {
        return createMsg();
    }

    @GET
    @Path("soups/resp")
    @Produces("multipart/mixed")
    public Response soupsResp() {
        return Response.ok(createMsg(), MediaType.valueOf("multipart/mixed"))
                .build();
    }

    @POST
    @Consumes("multipart/mixed")
    @Path("register/soups")
    public Response registerSoups(MultipartInput input) throws IOException {
        List<String> controlList = getControlList();

        for (InputPart inputPart : input.getParts()) {
            if (MediaType.APPLICATION_XML_TYPE.equals(inputPart.getMediaType())) {
                Soup c = inputPart.getBody(Soup.class, null);
                String name = c.getId();
                if (controlList.contains(name)) {
                    controlList.remove(name);
                }
            } else {
                String name = inputPart.getBody(String.class, null);
                if (controlList.contains(name)) {
                    controlList.remove(name);
                }
            }
        }

        // verify content and report to test
        StringBuilder sb = new StringBuilder();
        if (controlList.isEmpty()) {
            sb.append("success");
        } else {
            sb.append("Failed: parts not found: ");
            for (Iterator<String> it = controlList.iterator(); it.hasNext(); ) {
                sb.append(it.next() + " ");
            }
        }
        return Response.ok(sb.toString()).build();
    }

    @GET
    @Path("souplist/obj")
    @Produces("multipart/mixed")
    public MultipartOutput soupsListObj() {
        return createGenericTypeMsg();
    }

    @GET
    @Path("souplist/resp")
    @Produces("multipart/mixed")
    public Response soupsListResp() {
        return Response.ok(createGenericTypeMsg(),
                MediaType.valueOf("multipart/mixed"))
                .build();
    }

    private MultipartOutput createGenericTypeMsg() {
        MultipartOutput multipartOutput = new MultipartOutput();
        List<Soup> soupList = new ArrayList<Soup>();
        soupList.add(new Soup("Chicken Noodle"));
        soupList.add(new Soup("Vegetable"));
        multipartOutput.addPart(soupList, new GenericType<List<Soup>>(){},
                MediaType.APPLICATION_XML_TYPE );
        multipartOutput.addPart("Granny's Soups", MediaType.TEXT_PLAIN_TYPE);
        return multipartOutput;
    }

    @GET
    @Path("soupfile")
    @Produces("multipart/mixed")
    public MultipartOutput soupsFile() {
        // alternative to reading in file contents
        StringBuilder sb = new StringBuilder();
        sb.append("Chicken Noodle\n");
        sb.append("Vegetable\n");
        sb.append("Cuban Bean\n");

        MultipartOutput multipartOutput = new MultipartOutput();
        multipartOutput.addPart(sb.toString(), MediaType.TEXT_PLAIN_TYPE,
                "Grannys_Soup_Inventory.txt", true);

        return multipartOutput;
    }
}
