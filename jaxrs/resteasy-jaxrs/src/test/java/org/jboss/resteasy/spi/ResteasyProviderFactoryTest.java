package org.jboss.resteasy.spi;

import org.junit.Test;

import javax.ws.rs.ext.ContextResolver;

public class ResteasyProviderFactoryTest  {

	@Test
	public void testRegisterProvider() throws Exception {

		ResteasyProviderFactory factory = new ResteasyProviderFactory();
		factory.register(new ContextResolver<String>() {

			@Override
			public String getContext(Class<?> type) {
				return "foo bar";
			}
		});

	}

	@Test
	public void testRegisterProviderAsLambda() throws Exception {

		ResteasyProviderFactory factory = new ResteasyProviderFactory();
		factory.register((ContextResolver<String>) type -> "foo bar");

	}
}
