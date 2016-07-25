package org.jboss.resteasy.test.providers.jaxb.resource;

import org.junit.Assert;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("/jaxb")
@Consumes(
        {"application/xml", "application/fastinfoset", "application/json"})
@Produces(
        {"application/xml", "application/fastinfoset", "application/json"})
public class JaxbXmlRootElementProviderResource {

    @GET
    @Path("/{name}")
    public Parent getParent(@PathParam("name") String name) {
        Parent parent = Parent.createTestParent(name);
        return parent;
    }

    @GET
    @Path("/{name}")
    @Produces("application/junk+xml")
    public Parent getParentJunk(@PathParam("name") String name) {
        Parent parent = Parent.createTestParent(name);
        return parent;
    }

    @POST
    public Parent postParent(Parent parent) {
        Assert.assertTrue(parent.getChildren().size() > 0);
        Assert.assertTrue(parent.getChildren().get(0).getParent().equals(parent));
        parent.addChild(new Child("Child 4"));
        return parent;
    }
}
