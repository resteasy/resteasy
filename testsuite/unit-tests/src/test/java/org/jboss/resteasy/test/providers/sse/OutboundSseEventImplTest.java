package org.jboss.resteasy.test.providers.sse;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.OutboundSseEvent;

import org.jboss.resteasy.plugins.providers.sse.OutboundSseEventImpl;
import org.jboss.resteasy.test.providers.resource.ContractsData;
import org.junit.Assert;
import org.junit.Test;

/***
 *
 * @author Nicolas NESMON
 *
 */
public class OutboundSseEventImplTest {

	@Test
	public void Should_ReturnTextPlainMediaType_When_MediaTypeIsNotSet() throws Exception {
		OutboundSseEvent outboundSseEvent = new OutboundSseEventImpl.BuilderImpl().comment("comment").build();
		Assert.assertEquals(MediaType.TEXT_PLAIN_TYPE, outboundSseEvent.getMediaType());
	}

	@Test(expected = NullPointerException.class)
	public void Should_ThrowNullPointerException_When_MediaTypeIsNull() throws Exception {
		new OutboundSseEventImpl.BuilderImpl().mediaType(null).build();
	}

	@Test
	public void testGetOutboundSseEventGetMediaType() throws Exception {
		OutboundSseEvent outboundSseEvent = new OutboundSseEventImpl.BuilderImpl()
				.mediaType(MediaType.APPLICATION_XML_TYPE).comment("comment").build();
		Assert.assertEquals(MediaType.APPLICATION_XML_TYPE, outboundSseEvent.getMediaType());
	}

	@Test()
	public void Should_ThrowNullPointerException_When_DataIsNull() throws Exception {
		OutboundSseEvent.Builder builder = new OutboundSseEventImpl.BuilderImpl();
		try {
			builder.data(String.class, null);
			Assert.fail("A NullPointerException was expected");
		} catch (NullPointerException e) {
		}
		try {
			builder.data(new GenericType<String>() {
			}, null);
			Assert.fail("A NullPointerException was expected");
		} catch (NullPointerException e) {
		}
		try {
			builder.data(null);
			Assert.fail("A NullPointerException was expected");
		} catch (NullPointerException e) {
		}
	}

	@Test()
	public void Should_ThrowNullPointerException_When_TypeOrGenericTypeIsNull() throws Exception {
		OutboundSseEvent.Builder builder = new OutboundSseEventImpl.BuilderImpl();
		try {
			builder.data((Class<?>) null, "data");
			Assert.fail("A NullPointerException was expected");
		} catch (NullPointerException e) {
		}
		try {
			builder.data((GenericType<?>) null, "data");
			Assert.fail("A NullPointerException was expected");
		} catch (NullPointerException e) {
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void Should_ThrowIllegalArgumentException_When_CommentAndDataAreNull() throws Exception {
		OutboundSseEvent.Builder builder = new OutboundSseEventImpl.BuilderImpl();
		builder.comment("comment").build();
		builder.comment(null).build();
	}

	@Test()
	public void Should_ReturnTheExactDataType_When_DataIsAGenericEntity() throws Exception {
		GenericEntity<List<String>> genericEntity = new GenericEntity<List<String>>(new ArrayList<>()) {
		};
		OutboundSseEvent outboundSseEvent = new OutboundSseEventImpl.BuilderImpl().data(genericEntity).build();
		Assert.assertEquals(ArrayList.class, genericEntity.getRawType());
		Assert.assertEquals(genericEntity.getRawType(), outboundSseEvent.getType());
		Assert.assertEquals(genericEntity.getType(), outboundSseEvent.getGenericType());
	}

	@Test
	public void testGetOutboundSseEventGetType() throws Exception {
		OutboundSseEvent outboundSseEvent = new OutboundSseEventImpl.BuilderImpl()
				.data(ContractsData.class, new ContractsData()).build();
		Assert.assertEquals(ContractsData.class, outboundSseEvent.getType());
		Assert.assertEquals(ContractsData.class, outboundSseEvent.getGenericType());
	}

	@Test
	public void testGetOutboundSseEventGetTypeNull() throws Exception {
		OutboundSseEvent outboundSseEvent = new OutboundSseEventImpl.BuilderImpl()
				.comment("comment").build();
		Assert.assertEquals(null, outboundSseEvent.getType());
	}
}
