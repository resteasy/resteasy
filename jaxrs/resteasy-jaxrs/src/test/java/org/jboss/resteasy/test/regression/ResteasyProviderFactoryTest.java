package org.jboss.resteasy.test.regression;

import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.StringParameterUnmarshaller;
import org.junit.Before;
import org.junit.Test;

import java.lang.annotation.Annotation;
import java.sql.Date;

import static org.junit.Assert.*;
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
}
