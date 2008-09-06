/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.resteasy.test.providers.jaxb;

import org.jboss.resteasy.annotations.providers.jaxb.json.Mapped;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 * A JAXBXmlRootElementResource.
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
@Path("/jaxb")
@Consumes(
        {"application/xml", "application/fastinfoset", "application/json"})
@Produces(
        {"application/xml", "application/fastinfoset", "application/json"})
public class JAXBXmlRootElementResource
{

   @GET
   @Path("/{name}")
   public Parent getParent(@PathParam("name")String name)
   {
      Parent parent = Parent.createTestParent(name);
      return parent;
   }

   @GET
   @Path("/{name}")
   @Produces("application/junk+xml")
   public Parent getParentJunk(@PathParam("name")String name)
   {
      Parent parent = Parent.createTestParent(name);
      return parent;
   }

   @GET
   @Path("/{name}/mapped")
   @Mapped
   public Parent getMappedParent(@PathParam("name")String name)
   {
      System.out.println("MAPPED!!!!");
      Parent parent = Parent.createTestParent(name);
      return parent;
   }

   @POST
   public Parent postParent(Parent parent)
   {
      assert parent.getChildren().size() > 0;
      assert parent.getChildren().get(0).getParent().equals(parent);
      parent.addChild(new Child("Child 4"));
      return parent;
   }

}
