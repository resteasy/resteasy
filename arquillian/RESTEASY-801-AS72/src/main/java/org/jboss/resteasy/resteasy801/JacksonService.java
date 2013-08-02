package org.jboss.resteasy.resteasy801;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/products")
public class JacksonService
{
	@GET
	@Produces("application/json")
	@Path("{id}")
	public Product getProduct()
	{
		return new Product(333, "Iphone");
	}

	@GET
	@Produces("application/json")
	public Product[] getProducts()
	{

		Product[] products = {new Product(333, "Iphone"), new Product(44, "macbook")};
		return products;
	}

	@POST
	@Produces("application/foo+json")
	@Consumes("application/foo+json")
	@Path("{id}")
	public Product post(Product p)
	{
		return p;
	}

}
