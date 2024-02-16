package org.jboss.resteasy.test.providers.sse;

import java.util.ArrayList;
import java.util.List;

import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.OutboundSseEvent;

import org.jboss.resteasy.plugins.providers.sse.OutboundSseEventImpl;
import org.jboss.resteasy.test.providers.resource.ContractsData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/***
 *
 * @author Nicolas NESMON
 *
 */
public class OutboundSseEventImplTest {

    @Test
    public void Should_ReturnTextPlainMediaType_When_MediaTypeIsNotSet() throws Exception {
        OutboundSseEvent outboundSseEvent = new OutboundSseEventImpl.BuilderImpl().comment("comment").build();
        Assertions.assertEquals(MediaType.TEXT_PLAIN_TYPE, outboundSseEvent.getMediaType());
    }

    @Test
    public void Should_ThrowNullPointerException_When_MediaTypeIsNull() throws Exception {
        NullPointerException thrown = Assertions.assertThrows(NullPointerException.class,
                () -> {
                    new OutboundSseEventImpl.BuilderImpl().mediaType(null).build();
                });
        Assertions.assertTrue(thrown instanceof NullPointerException);
    }

    @Test
    public void testGetOutboundSseEventGetMediaType() throws Exception {
        OutboundSseEvent outboundSseEvent = new OutboundSseEventImpl.BuilderImpl()
                .mediaType(MediaType.APPLICATION_XML_TYPE).comment("comment").build();
        Assertions.assertEquals(MediaType.APPLICATION_XML_TYPE, outboundSseEvent.getMediaType());
    }

    @Test()
    public void Should_ThrowNullPointerException_When_DataIsNull() throws Exception {
        OutboundSseEvent.Builder builder = new OutboundSseEventImpl.BuilderImpl();
        try {
            builder.data(String.class, null);
            Assertions.fail("A NullPointerException was expected");
        } catch (NullPointerException e) {
        }
        try {
            builder.data(new GenericType<String>() {
            }, null);
            Assertions.fail("A NullPointerException was expected");
        } catch (NullPointerException e) {
        }
        try {
            builder.data(null);
            Assertions.fail("A NullPointerException was expected");
        } catch (NullPointerException e) {
        }
    }

    @Test()
    public void Should_ThrowNullPointerException_When_TypeOrGenericTypeIsNull() throws Exception {
        OutboundSseEvent.Builder builder = new OutboundSseEventImpl.BuilderImpl();
        try {
            builder.data((Class<?>) null, "data");
            Assertions.fail("A NullPointerException was expected");
        } catch (NullPointerException e) {
        }
        try {
            builder.data((GenericType<?>) null, "data");
            Assertions.fail("A NullPointerException was expected");
        } catch (NullPointerException e) {
        }
    }

    @Test
    public void Should_ThrowIllegalArgumentException_When_CommentAndDataAreNull() throws Exception {
        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class,
                () -> {
                    OutboundSseEvent.Builder builder = new OutboundSseEventImpl.BuilderImpl();
                    builder.comment("comment").build();
                    builder.comment(null).build();
                });
        Assertions.assertTrue(thrown instanceof IllegalArgumentException);
    }

    @Test()
    public void Should_ReturnTheExactDataType_When_DataIsAGenericEntity() throws Exception {
        GenericEntity<List<String>> genericEntity = new GenericEntity<List<String>>(new ArrayList<>()) {
        };
        OutboundSseEvent outboundSseEvent = new OutboundSseEventImpl.BuilderImpl().data(genericEntity).build();
        Assertions.assertEquals(ArrayList.class, genericEntity.getRawType());
        Assertions.assertEquals(genericEntity.getRawType(), outboundSseEvent.getType());
        Assertions.assertEquals(genericEntity.getType(), outboundSseEvent.getGenericType());
    }

    @Test
    public void testGetOutboundSseEventGetType() throws Exception {
        OutboundSseEvent outboundSseEvent = new OutboundSseEventImpl.BuilderImpl()
                .data(ContractsData.class, new ContractsData()).build();
        Assertions.assertEquals(ContractsData.class, outboundSseEvent.getType());
        Assertions.assertEquals(ContractsData.class, outboundSseEvent.getGenericType());
    }

    @Test
    public void testGetOutboundSseEventGetTypeNull() throws Exception {
        OutboundSseEvent outboundSseEvent = new OutboundSseEventImpl.BuilderImpl()
                .comment("comment").build();
        Assertions.assertEquals(null, outboundSseEvent.getType());
    }
}
