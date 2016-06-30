package org.jboss.resteasy.test.providers.jaxb;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ContextResolver;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class XmlJAXBContextFinderTest {

	@XmlRootElement
	@XmlAccessorType(XmlAccessType.FIELD)
	private static final class BeanWrapper {

		@XmlAnyElement(lax = true)
		private Object bean;

		public Object getBean() {
			return this.bean;
		}

		public void setBean(Object bean) {
			this.bean = bean;
		}

	}

	@XmlRootElement
	@XmlAccessorType(XmlAccessType.FIELD)
	private static final class FirstBean {

		private String data;

		public String getData() {
			return this.data;
		}

		public void setData(String data) {
			this.data = data;
		}

	}

	@XmlRootElement
	@XmlAccessorType(XmlAccessType.FIELD)
	private static final class SecondBean {

		private String data;

		public String getData() {
			return this.data;
		}

		public void setData(String data) {
			this.data = data;
		}

	}

	@Path("/firstTestResource")
	@Produces(MediaType.APPLICATION_XML)
	public static final class FirstTestResource {

		@GET
		public Response get() {
			FirstBean firstBean = new FirstBean();
			firstBean.setData("firstTestResource");
			BeanWrapper beanWrapper = new BeanWrapper();
			beanWrapper.setBean(firstBean);
			return Response.ok(beanWrapper).build();
		}

	}

	@Path("/secondTestResource")
	@Produces(MediaType.APPLICATION_XML)
	public static final class SecondTestResource {

		@GET
		public Response get() {
			SecondBean secondBean = new SecondBean();
			secondBean.setData("secondTestResource");
			BeanWrapper beanWrapper = new BeanWrapper();
			beanWrapper.setBean(secondBean);
			return Response.ok(beanWrapper).build();
		}

	}

	private static ResteasyDeployment deployment;
	private static Dispatcher dispatcher;

	@BeforeClass
	public static void before() throws Exception {
		deployment = EmbeddedContainer.start();
		ContextResolver<JAXBContext> jaxbContextResolver = new ContextResolver<JAXBContext>() {

			private JAXBContext jaxbContext;

			@Override
			public JAXBContext getContext(Class<?> type) {
				if (this.jaxbContext == null) {
					try {
						this.jaxbContext = JAXBContext
								.newInstance(BeanWrapper.class, FirstBean.class, SecondBean.class);
					} catch (JAXBException e) {
					}
				}
				return this.jaxbContext;
			}

		};
		deployment.getProviderFactory().register(jaxbContextResolver);
		dispatcher = deployment.getDispatcher();
		Registry registry = deployment.getRegistry();
		registry.addPerRequestResource(FirstTestResource.class);
		registry.addPerRequestResource(SecondTestResource.class);
	}

	@AfterClass
	public static void after() throws Exception {
		EmbeddedContainer.stop();
		dispatcher = null;
		deployment = null;
	}

	// In the following test both firstWebTarget and secondWebTarget will share
	// the same XmlJAXBContextFinder inherited from their shared
	// parent configuration.
	// We define and register a ContextResolver<JAXBContext> for each webTarget
	// so that firstWebTarget and secondWebTarget have its own (respectively
	// firstJaxbContextResolver and secondJaxbContextResolver).
	@Test
	public void test() {
		Client client = ClientBuilder.newClient();
		try {
			// Fist webTarget
			WebTarget firstWebTarget = client.target("http://localhost:8081/firstTestResource");
			ContextResolver<JAXBContext> firstJaxbContextResolver = new ContextResolver<JAXBContext>() {

				private JAXBContext jaxbContext;

				@Override
				public JAXBContext getContext(Class<?> type) {
					if (this.jaxbContext == null) {
						try {
							this.jaxbContext = JAXBContext.newInstance(BeanWrapper.class, FirstBean.class);
						} catch (JAXBException e) {
						}
					}
					return this.jaxbContext;
				}

			};
			Response firstResponse = firstWebTarget.register(firstJaxbContextResolver)
					.request(MediaType.APPLICATION_XML_TYPE).get();
			BeanWrapper firstBeanWrapper = firstResponse.readEntity(BeanWrapper.class);
			Assert.assertTrue(FirstBean.class.isAssignableFrom(firstBeanWrapper.getBean().getClass()));

			// Second webTarget
			WebTarget secondWebTarget = client.target("http://localhost:8081/secondTestResource");
			// Will never be called
			ContextResolver<JAXBContext> secondJaxbContextResolver = new ContextResolver<JAXBContext>() {

				private JAXBContext jaxbContext;

				@Override
				public JAXBContext getContext(Class<?> type) {
					if (this.jaxbContext == null) {
						try {
							this.jaxbContext = JAXBContext.newInstance(BeanWrapper.class, SecondBean.class);
						} catch (JAXBException e) {
						}
					}
					return this.jaxbContext;
				}

			};
			Response secondResponse = secondWebTarget.register(secondJaxbContextResolver)
					.request(MediaType.APPLICATION_XML_TYPE).get();
			BeanWrapper secondBeanWrapper = secondResponse.readEntity(BeanWrapper.class);
			Assert.assertTrue(SecondBean.class.isAssignableFrom(secondBeanWrapper.getBean().getClass()));
		} finally {
			client.close();
		}
	}

}
