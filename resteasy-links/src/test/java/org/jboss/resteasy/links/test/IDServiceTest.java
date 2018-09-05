package org.jboss.resteasy.links.test;

import org.jboss.resteasy.links.AddLinks;
import org.jboss.resteasy.links.LinkResource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Produces("application/xml")
@Path("/")
public interface IDServiceTest {
	@GET
	@AddLinks
	@LinkResource
	@Path("jpa-id/book/{name}")
	JpaIdBook getJpaIdBook(@PathParam("name") String name);

	@GET
	@AddLinks
	@LinkResource
	@Path("xml-id/book/{name}")
	XmlIdBook getXmlIdBook(@PathParam("name")String name);


	@GET
	@AddLinks
	@LinkResource
	@Path("resource-id/book/{name}")
	ResourceIdBook getResourceIdBook(@PathParam("name")String name);


	@GET
	@AddLinks
	@LinkResource
	@Path("resource-ids/book/{namea}/{nameb}")
	ResourceIdsBook getResourceIdsBook(@PathParam("namea")String namea, @PathParam("nameb")String nameb);

	@GET
	@AddLinks
	@LinkResource
	@Path("resource-id-method/book/{name}")
	ResourceIdMethodBook getResourceIdMethodBook(@PathParam("name")String name);


	@GET
	@AddLinks
	@LinkResource
	@Path("resource-ids-method/book/{namea}/{nameb}")
	ResourceIdsMethodBook getResourceIdsMethodBook(@PathParam("namea")String namea, @PathParam("nameb")String nameb);

}
