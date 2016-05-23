package org.jboss.resteasy.test.regression;

import static org.junit.Assert.assertNotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.Date;
import java.util.List;

import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;

import org.jboss.resteasy.core.interception.JaxrsInterceptorRegistry.InterceptorFactory;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.StringParameterUnmarshaller;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
/**
 * resteasy-584
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ResteasyProviderFactoryTest
{
	private ResteasyProviderFactory factory;

	@Before
	public void createBean() {
		factory = new ResteasyProviderFactory();
	}

	@Test
	public void shouldReturnStringParameterUnmarshallerAddedForType() {
		factory.addStringParameterUnmarshaller(MyStringParameterUnmarshaller.class);

		assertNotNull(factory.createStringParameterUnmarshaller(Date.class));
	}

	public static class MyStringParameterUnmarshaller implements StringParameterUnmarshaller<Date>
   {

		@Override
		public void setAnnotations(Annotation[] annotations) {
	}

		@Override
		public Date fromString(String str) {
			return null;
		}

	}
	
	/**
	 * Test case for bug RESTEASY-1311.
	 * Test whether the priority is supplied to the container request filter registry.
	 */
	@Test
	public void testRegisterProviderInstancePriorityContainerRequestFilter() throws Exception {
		ContainerRequestFilter requestFilter = new ContainerRequestFilter() {
			public void filter(ContainerRequestContext requestContext) {}
		};
		this.testRegisterProviderInstancePriority(requestFilter, factory.getContainerRequestFilterRegistry());
	}
	
	/**
	 * Test case for bug RESTEASY-1311.
	 * Test whether the priority is supplied to the container response filter registry.
	 */
	@Test
	public void testRegisterProviderInstancePriorityContainerResponseFilter() throws Exception {
		ContainerResponseFilter responseFilter = new ContainerResponseFilter() {
			public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {}
		};
		this.testRegisterProviderInstancePriority(responseFilter, factory.getContainerResponseFilterRegistry());
	}
	
	/**
	 * Generic helper method for RESTEASY-1311 cases, because the test logic is the same.
	 * Unfortunately, there seems to be no public accessors for the properties we need,
	 * so we have to resort to using reflection to check the right priority setting.
	 */
	private void testRegisterProviderInstancePriority(Object filter, Object registry) throws Exception {
		int priorityOverride = Priorities.USER + 1;
		factory.registerProviderInstance(filter, null, priorityOverride, false);
		
		Field interceptorsField = registry.getClass().getSuperclass().getDeclaredField("interceptors");
		interceptorsField.setAccessible(true);
		@SuppressWarnings("unchecked")
		List<InterceptorFactory> interceptors = (List<InterceptorFactory>) interceptorsField.get(registry);
		
		Field orderField = interceptors.get(0).getClass().getSuperclass().getDeclaredField("order");
		orderField.setAccessible(true);
		int order = (Integer) orderField.get(interceptors.get(0));
		Assert.assertEquals(priorityOverride, order);
	}
}
