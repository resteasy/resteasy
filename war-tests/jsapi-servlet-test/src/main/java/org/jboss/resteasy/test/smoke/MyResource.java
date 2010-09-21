package org.jboss.resteasy.test.smoke;

import org.jboss.resteasy.annotations.providers.jaxb.Wrapped;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.LinkedList;
import java.util.List;

@Path("/mine")
public class MyResource
{

	@GET
	public String get()
	{
		return "ok";
	}

	@Path("foo")
	@GET
	public String getFoo()
	{
		return "foo";
	}

	@Path("{param}")
	@GET
	public String getParam(@PathParam("param") String param)
	{
		return param;
	}

	@Path("foo/{param}-{other}")
	@GET
	public String getFooParam(@PathParam("param") String param,
			@PathParam("other") String other,
			@QueryParam("q") String q,
			@CookieParam("c") String c,
			@HeaderParam("h") String h,
			@MatrixParam("m") String m,
			@Context UriInfo ignore)
	{
		StringBuffer buf = new StringBuffer();
		buf.append("param").append("=").append(param).append(";");
		buf.append("other").append("=").append(other).append(";");
		buf.append("q").append("=").append(q).append(";");
		buf.append("c").append("=").append(c).append(";");
		buf.append("h").append("=").append(h).append(";");
		buf.append("m").append("=").append(m).append(";");
		return buf.toString();
	}

	@Path("foo/{param}-{other}")
	@PUT
	public String putFooParam(@PathParam("param") String param,
			@PathParam("other") String other,
			@QueryParam("q") String q,
			@CookieParam("c") String c,
			@HeaderParam("h") String h,
			@MatrixParam("m") String m,
			String entity,
			@Context UriInfo ignore)
	{
		StringBuffer buf = new StringBuffer();
		buf.append("param").append("=").append(param).append(";");
		buf.append("other").append("=").append(other).append(";");
		buf.append("q").append("=").append(q).append(";");
		buf.append("c").append("=").append(c).append(";");
		buf.append("h").append("=").append(h).append(";");
		buf.append("m").append("=").append(m).append(";");
		buf.append("entity").append("=").append(entity).append(";");
		return buf.toString();
	}

	@XmlRootElement
	public static class Test
	{
		private String var;

		public Test()
		{
		}

		public Test(String var)
		{
			this.var = var;
		}

		@XmlElement
		public String getVar()
		{
			return var;
		}

		public void setVar(String var)
		{
			this.var = var;
		}
	}

	@Path("multi")
	@GET
	@Produces({"application/json", "application/xml"})
	public Test getMulti(@Context Request request)
	{
		return new Test("foo");
	}

	@Path("json")
	@GET
	@Produces({"application/json"})
	public Test getJSON()
	{
		return new Test("foo");
	}

	@Path("json-star-mime")
	@GET
	@Produces({"application/*+json"})
	public Test getJSONStarMIME()
	{
		return new Test("foo");
	}

	@Path("xml")
	@GET
	@Produces({"application/xml"})
	public Test getXML()
	{
		return new Test("foo");
	}

	@Path("xml")
	@PUT
	@Consumes({"application/xml"})
	@Produces("text/plain")
	public String putXML(Test test)
	{
		if (test != null && "ok".equals(test.var))
			return "ok";
		return "fail";
	}

	@Path("json")
	@PUT
	@Consumes({"application/json"})
	@Produces("text/plain")
	public String putJSON(Test test)
	{
		if (test != null && "ok".equals(test.var))
			return "ok";
		return "fail";
	}

	@XmlRootElement
	public static class Pair
	{
		private String key;
		private String value;

		public Pair()
		{
		}

		public Pair(String key, String value)
		{
			this.key = key;
			this.value = value;
		}

		@XmlElement
		public String getKey()
		{
			return key;
		}

		public void setKey(String key)
		{
			this.key = key;
		}

		@XmlElement
		public String getValue()
		{
			return value;
		}

		public void setValue(String value)
		{
			this.value = value;
		}
	}

	@Produces({"application/xml", "application/json", "application/x-yaml"})
	@Wrapped(element = "foo")
	@Path("pairs")
	@GET
	public List<Pair> getMultiRepresentation()
	{
		List<Pair> pairs = new LinkedList<Pair>();
		for (int i = 0; i < 23; i++)
			pairs.add(new Pair("key" + i, "value" + i));
		return pairs;
	}

	@GET
	@Path("lookup")
	public String lookup(@QueryParam("id") String id,
			@Context UriInfo uriInfo)
	{
		return uriInfo.getBaseUriBuilder().path(getClass()).path(getClass(), "getParam").build(id).toString();
	}

	public static class SubResource {
		@GET
		public String get(){
			return "Hello";
		}
		@Path("sub")
		@GET
		public String getWithPath(){
			return "Hello withPath";
		}
	}

	@Path("sub-resource")
	public SubResource getSubResource(){
		return new SubResource();
	}

	@Path("sub-resource2/{id}")
	public SubResource2 getSubResource2(@PathParam("id") String id){
		return new SubResource2(id);
	}

	@Path("more/{foo}")
	public static class SubResource2 {
		private String id;
		public SubResource2(String id) {
			this.id = id;
		}
		@GET
		public String get(@PathParam("foo") String foo){
			return "Hello "+id+"/"+foo;
		}
		@Path("sub/{bar}")
		@GET
		public String getWithPath(@PathParam("foo") String foo, @PathParam("bar") String bar){
			return "Hello withPath "+id+"/"+foo+"/"+bar;
		}
		
		@Path("subsub")
		public SubResource getSubResource(){
			return new SubResource();
		}
	}
}