package org.jboss.resteasy.plugins.providers.sse;

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

}
