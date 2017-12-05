package org.jboss.resteasy.plugins.providers.sse;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.OutboundSseEvent;

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

}
