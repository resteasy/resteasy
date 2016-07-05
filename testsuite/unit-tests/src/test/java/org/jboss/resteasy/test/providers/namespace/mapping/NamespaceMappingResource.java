package org.jboss.resteasy.test.providers.namespace.mapping;

import org.jboss.resteasy.annotations.providers.jaxb.json.Mapped;
import org.jboss.resteasy.annotations.providers.jaxb.json.XmlNsMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/test/v1")
public class NamespaceMappingResource {
    @POST
    @Consumes("application/*+json")
    @Produces("application/*+json")
    @Mapped(namespaceMap = {
            @XmlNsMap(jsonName = "test", namespace = "http://www.example.org/b/Test"),
            @XmlNsMap(jsonName = "can", namespace = "http://www.example.org/a/TestCanonical")
    })
    public NamespaceMappingTestExtends updateTestExtends(@Mapped(namespaceMap = {
            @XmlNsMap(jsonName = "test", namespace = "http://www.example.org/b/Test"),
            @XmlNsMap(jsonName = "can", namespace = "http://www.example.org/a/TestCanonical")
    }) NamespaceMappingTestExtends data) {
        return data;
    }

    @GET
    @Produces("application/*+json")
    @Mapped(namespaceMap = {
            @XmlNsMap(jsonName = "test", namespace = "http://www.example.org/b/Test"),
            @XmlNsMap(jsonName = "can", namespace = "http://www.example.org/a/TestCanonical")
    })
    public NamespaceMappingTestExtends getTestExtends() {
        NamespaceMappingTestExtends result = new NamespaceMappingTestExtends();
        result.setId("12121");
        result.setName("Test");
        result.setDesc("Desc");
        result.setElement2("Test");
        result.setSomeMoreEl("test");
        return result;
    }

    @Path("/manual")
    @Produces("application/*+json")
    @GET
    public String getManual() {
        return null;
    }

}
