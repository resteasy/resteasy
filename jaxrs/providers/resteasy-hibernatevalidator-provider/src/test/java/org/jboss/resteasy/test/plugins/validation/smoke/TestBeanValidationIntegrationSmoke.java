package org.jboss.resteasy.test.plugins.validation.smoke;

import junit.framework.Assert;
import org.jboss.resteasy.annotations.Form;
import org.jboss.resteasy.client.ClientResponseFailure;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.plugins.server.resourcefactory.POJOResourceFactory;
import org.jboss.resteasy.plugins.validation.hibernate.DoNotValidateRequest;
import org.jboss.resteasy.plugins.validation.hibernate.ValidateRequest;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import static org.jboss.resteasy.test.TestPortProvider.generateBaseUrl;

public class TestBeanValidationIntegrationSmoke {

	// GROUPS
	public static interface UpdatePhase {
	}

	public static interface InsertPhase {
	}

	public static class InputBean {

		@QueryParam("id")
		@NotNull(groups = UpdatePhase.class)
		private Long id;

		@QueryParam("name")
		@NotNull(groups = InsertPhase.class)
		private String name;

		@QueryParam("age")
		@Max(value = 80, groups = InsertPhase.class)
		private Integer age;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Integer getAge() {
			return age;
		}

		public void setAge(Integer age) {
			this.age = age;
		}

		@Override
		public String toString() {
			return "InputBean [id=" + id + ", name=" + name + ", age=" + age
					+ "]";
		}

	}

	@Path("shouldNotValidateResource")
	public static interface ShouldNotValidateResource {
		@POST
		@Path("toString/{p1}/{p2}")
		public String sumPost(
				@PathParam("p1") @Pattern(regexp = "\\d+") String p1,
				@PathParam("p2") @Pattern(regexp = "\\d+") String p2);
		
		@GET
		@Path("sum")
		public String sumGet(
				@QueryParam("p1") @Pattern(regexp = "\\d+") String p1,
				@QueryParam("p2") @Pattern(regexp = "\\d+") String p2);
		
		public static class Impl implements ShouldNotValidateResource{
			public String sumPost(String p1, String p2) {
				try {
					return "sum = " + (Integer.valueOf(p1) + Integer.valueOf(p2));
				} catch (NumberFormatException e) {
					return "bad numbers";
				}
			}

			public String sumGet(String p1, String p2) {
				try {
					return "sum = " + (Integer.valueOf(p1) + Integer.valueOf(p2));
				} catch (NumberFormatException e) {
					return "bad numbers";
				}
			}
		}
	}
	
	@Path("shouldValidateJustOneMethod")
	public static interface ShouldValidateJustOneMethod {
		
		@POST
		@Path("sum/{p1}/{p2}")
		public String sumPost(
				@PathParam("p1") @Pattern(regexp = "\\d+") String p1,
				@PathParam("p2") @Pattern(regexp = "\\d+") String p2);
		
		@GET
		@Path("sum")
		@ValidateRequest
		public String sumGet(
				@QueryParam("p1") @Pattern(regexp = "\\d+") String p1,
				@QueryParam("p2") @Pattern(regexp = "\\d+") String p2);
		
		public static class Impl implements ShouldValidateJustOneMethod{
			public String sumPost(String p1, String p2) {
				try {
					return "sum = " + (Integer.valueOf(p1) + Integer.valueOf(p2));
				} catch (NumberFormatException e) {
					return "bad numbers";
				}
			}

			public String sumGet(String p1, String p2) {
				try {
					return "sum = " + (Integer.valueOf(p1) + Integer.valueOf(p2));
				} catch (NumberFormatException e) {
					return "bad numbers";
				}
			}
		}
	}
	
	@Path("shouldValidateAllMethods")
	@ValidateRequest
	public static interface ShouldValidateAllMethods {
		
		@POST
		@Path("sum/{p1}/{p2}")
		public String sumPost(
				@PathParam("p1") @Pattern(regexp = "\\d+") String p1,
				@PathParam("p2") @Pattern(regexp = "\\d+") String p2);
		
		@GET
		@Path("sum")
		public String sumGet(
				@QueryParam("p1") @Pattern(regexp = "\\d+") String p1,
				@QueryParam("p2") @Pattern(regexp = "\\d+") String p2);
		
		public static class Impl implements ShouldValidateAllMethods{
			public String sumPost(String p1, String p2) {
				try {
					return "sum = " + (Integer.valueOf(p1) + Integer.valueOf(p2));
				} catch (NumberFormatException e) {
					return "bad numbers";
				}
			}

			public String sumGet(String p1, String p2) {
				try {
					return "sum = " + (Integer.valueOf(p1) + Integer.valueOf(p2));
				} catch (NumberFormatException e) {
					return "bad numbers";
				}
			}
		}
	}
	
	@Path("shouldNotValidateOneMethod")
	@ValidateRequest
	public static interface ShouldNotValidateOneMethod {
		
		@POST
		@Path("sum/{p1}/{p2}")
		@DoNotValidateRequest
		public String sumPost(
				@PathParam("p1") @Pattern(regexp = "\\d+") String p1,
				@PathParam("p2") @Pattern(regexp = "\\d+") String p2);
		
		@GET
		@Path("sum")
		public String sumGet(
				@QueryParam("p1") @Pattern(regexp = "\\d+") String p1,
				@QueryParam("p2") @Pattern(regexp = "\\d+") String p2);
		
		public static class Impl implements ShouldNotValidateOneMethod{
			public String sumPost(String p1, String p2) {
				try {
					return "sum = " + (Integer.valueOf(p1) + Integer.valueOf(p2));
				} catch (NumberFormatException e) {
					return "bad numbers";
				}
			}

			public String sumGet(String p1, String p2) {
				try {
					return "sum = " + (Integer.valueOf(p1) + Integer.valueOf(p2));
				} catch (NumberFormatException e) {
					return "bad numbers";
				}
			}
		}
	}
	
	@Path("shouldRespectGroups")
	@Consumes(MediaType.WILDCARD)
	@ValidateRequest
	public static interface ShouldRespectGroups {
		
		@POST
		@Path("insert")
		@ValidateRequest(groups = InsertPhase.class)
		public String insert(@Form InputBean input);
		
		@POST
		@Path("sum")
		@ValidateRequest(groups = UpdatePhase.class)
		public String update(@Form InputBean input);
		
		public static class Impl implements ShouldRespectGroups{
			public String insert(InputBean input) {
				return input.toString();
			}

			public String update(InputBean input) {
				return input.toString();
			}
		}
	}

	private static Dispatcher dispatcher;

	@BeforeClass
	public static void before() throws Exception {
		dispatcher = EmbeddedContainer.start().getDispatcher();
		
	}

	@AfterClass
	public static void after() throws Exception {
		EmbeddedContainer.stop();
	}
	
	@Before
	public void register() {
	}
	
	@Test
//	@Ignore
	public void shouldValidateNothing() {
		POJOResourceFactory factory = new POJOResourceFactory(ShouldNotValidateResource.Impl.class);
		dispatcher.getRegistry().addResourceFactory(factory);
		
		ShouldNotValidateResource resource = ProxyFactory.create(ShouldNotValidateResource.class, generateBaseUrl());
			
		Assert.assertEquals("bad numbers", resource.sumPost("a", "b"));
		Assert.assertEquals("bad numbers", resource.sumGet("c3", "2d"));
	}
	
	@Test
//	@Ignore
	public void shouldValidateJustOneMethod() {
		POJOResourceFactory noDefaults = new POJOResourceFactory(ShouldValidateJustOneMethod.Impl.class);
		dispatcher.getRegistry().addResourceFactory(noDefaults);
		
		ShouldValidateJustOneMethod resource = ProxyFactory.create(ShouldValidateJustOneMethod.class, generateBaseUrl());
		
		Assert.assertEquals("bad numbers", resource.sumPost("a", "b"));
		try {
			resource.sumGet("c3", "2d");
		} catch (ClientResponseFailure e) {
			Assert.assertEquals(400, e.getResponse().getStatus());
			// should validate more things, not just status code
		}
	}
	
	@Test
//	@Ignore
	public void shouldValidateAllMethods() {
		POJOResourceFactory noDefaults = new POJOResourceFactory(ShouldValidateAllMethods.Impl.class);
		dispatcher.getRegistry().addResourceFactory(noDefaults);
		
		ShouldValidateAllMethods resource = ProxyFactory.create(ShouldValidateAllMethods.class, generateBaseUrl());
		
		try {
			resource.sumPost("a", "b");
		} catch (ClientResponseFailure e) {
			Assert.assertEquals(400, e.getResponse().getStatus());
			// should validate more things, not just status code
		}
		try {
			resource.sumGet("c3", "2d");
		} catch (ClientResponseFailure e) {
			Assert.assertEquals(400, e.getResponse().getStatus());
			// should validate more things, not just status code
		}
	}
	
	@Test
//	@Ignore
	public void shouldNotValidateOneMethod() {
		POJOResourceFactory noDefaults = new POJOResourceFactory(ShouldNotValidateOneMethod.Impl.class);
		dispatcher.getRegistry().addResourceFactory(noDefaults);
		
		ShouldNotValidateOneMethod resource = ProxyFactory.create(ShouldNotValidateOneMethod.class, generateBaseUrl());
		
		Assert.assertEquals("bad numbers", resource.sumPost("a", "b"));
		try {
			resource.sumGet("c3", "2d");
		} catch (ClientResponseFailure e) {
			Assert.assertEquals(400, e.getResponse().getStatus());
			// should validate more things, not just status code
		}
	}
	
	@Test
//	@Ignore
	public void shouldRespectInsertGroup() {
		POJOResourceFactory noDefaults = new POJOResourceFactory(ShouldRespectGroups.Impl.class);
		dispatcher.getRegistry().addResourceFactory(noDefaults);
		
		ShouldRespectGroups resource = ProxyFactory.create(ShouldRespectGroups.class, generateBaseUrl());
		
		InputBean input = new InputBean();
		input.setId(null);
		input.setName("<<NAME>>");
		input.setAge(27);
		
		Assert.assertEquals(input.toString(), resource.insert(input));
		
		try {
			input.setAge(1000);
			resource.insert(input);
		} catch (ClientResponseFailure e) {
			Assert.assertEquals(400, e.getResponse().getStatus());
			// should validate more things, not just status code
		}
		
		try {
			input.setId(123L);
			input.setAge(27);
			resource.insert(input);
		} catch (ClientResponseFailure e) {
			Assert.assertEquals(400, e.getResponse().getStatus());
			// should validate more things, not just status code
		}
	}
}
