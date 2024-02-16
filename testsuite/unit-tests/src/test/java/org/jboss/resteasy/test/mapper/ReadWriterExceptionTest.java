package org.jboss.resteasy.test.mapper;

import java.util.HashSet;
import java.util.Set;

import jakarta.ws.rs.core.Response;

import org.jboss.resteasy.core.ExceptionHandler;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.plugins.providers.jaxb.JAXBMarshalException;
import org.jboss.resteasy.plugins.providers.jaxb.JAXBUnmarshalException;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.spi.ReaderException;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.WriterException;
import org.jboss.resteasy.test.mapper.resource.ReaderExceptionMapper;
import org.jboss.resteasy.test.mapper.resource.SprocketDBException;
import org.jboss.resteasy.test.mapper.resource.SprocketDBExceptionMapper;
import org.jboss.resteasy.test.mapper.resource.WriterExceptionMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test that the Exception processing and ExceptionMapper handling rules defined in
 * the jsr-311 section 3.3.4 are dealt with correctly. Related information is provided
 * in the Resteasy guide section 29.
 *
 * @tpSubChapter Exception-Handling
 * @tpChapter Unit tests
 * @tpSince RESTEasy 4.0.0
 */
public class ReadWriterExceptionTest {

    private Set<String> unwrappedExceptions = new HashSet<String>();

    @Test
    public void testWriterExceptionMapper() throws Exception {

        HttpRequest request = MockHttpRequest.get("/locating/basic");
        ResteasyProviderFactory factory = ResteasyProviderFactory.newInstance();

        ExceptionHandler eHandler = new ExceptionHandler(factory, unwrappedExceptions);

        // When exception response object is NULL and exception errorCode is -1 and
        // no exception cause object, return status code SC_INTERNAL_SERVER_ERROR.
        JAXBMarshalException jaxbEOne = new JAXBMarshalException("JAXBMarshalException's WriterException test one");
        Response resultOne = eHandler.handleException(request, jaxbEOne);
        Assertions.assertEquals(resultOne.getStatus(), HttpResponseCodes.SC_INTERNAL_SERVER_ERROR,
                "First WriterExceptionMapper: incorrect status code returned");

        // When exception response object is NULL and exception errorCode is -1 and
        // there is an exception cause object, return status code for the cause when
        // there is a mapper for that cause class.
        JAXBMarshalException jaxbETwo = new JAXBMarshalException("JAXBMarshalException's WriterException test one",
                new SprocketDBException());
        factory.registerProvider(SprocketDBExceptionMapper.class);
        Response resultTwo = eHandler.handleException(request, jaxbETwo);
        Assertions.assertEquals(resultTwo.getStatus(), SprocketDBExceptionMapper.STATUS_CODE,
                "Second WriterExceptionMapper: incorrect status code returned");

        // When there is no mapper for WriterException and exception errorCode is
        // greater than -1 return a response with that error code
        int customErrorCode = 111222333;
        WriterException wEOne = new WriterException("WriterException test", customErrorCode);
        Response resultThree = eHandler.handleException(request, wEOne);
        Assertions.assertEquals(resultThree.getStatus(), customErrorCode,
                "Third WriterExceptionMapper: incorrect status code returned");

        // JAXBMarshalException is a subclass of WriterException
        // A WriterException mapper is provided
        JAXBMarshalException jaxbE = new JAXBMarshalException("JAXBMarshalException's WriterException test",
                Response.status(WriterExceptionMapper.STATUS_CODE)
                        .entity("JAXBMarshalException response").build());

        factory.registerProvider(WriterExceptionMapper.class);
        Response result = eHandler.handleException(request, jaxbE);

        Assertions.assertEquals(result.getStatus(), WriterExceptionMapper.STATUS_CODE,
                "WriterExceptionMapper: incorrect status code returned");
    }

    @Test
    public void testReaderExceptionMapper() throws Exception {

        HttpRequest request = MockHttpRequest.get("/locating/basic");
        ResteasyProviderFactory factory = ResteasyProviderFactory.newInstance();

        ExceptionHandler eHandler = new ExceptionHandler(factory, unwrappedExceptions);

        // When exception response object is NULL and exception errorCode is -1 and
        // no exception cause object, return status code SC_BAD_REQUEST.
        JAXBUnmarshalException jaxbEOne = new JAXBUnmarshalException("JAXBUnmarshalException's ReaderException test one");
        Response resultOne = eHandler.handleException(request, jaxbEOne);
        Assertions.assertEquals(resultOne.getStatus(), HttpResponseCodes.SC_BAD_REQUEST,
                "First ReaderExceptionMapper: incorrect status code returned");

        // When exception response object is NULL and exception errorCode is -1 and
        // there is an exception cause object, return status code for the cause when
        // there is a mapper for that cause class.
        JAXBUnmarshalException jaxbETwo = new JAXBUnmarshalException("JAXBUnmarshalException's ReaderException test one",
                new SprocketDBException());
        factory.registerProvider(SprocketDBExceptionMapper.class);
        Response resultTwo = eHandler.handleException(request, jaxbETwo);
        Assertions.assertEquals(resultTwo.getStatus(), SprocketDBExceptionMapper.STATUS_CODE,
                "Second ReaderExceptionMapper: incorrect status code returned");

        // When exception errorCode is greater than -1 return a response with that error code
        // and there is no mapper for WriterException.
        int customErrorCode = 444555666;
        ReaderException wEOne = new ReaderException("ReaderException test", customErrorCode);
        Response resultThree = eHandler.handleException(request, wEOne);
        Assertions.assertEquals(resultThree.getStatus(), customErrorCode,
                "Third ReaderExceptionMapper: incorrect status code returned");

        // JAXBUnmarshalException is a subclass of ReaderException
        // A ReaderException mapper is provided
        JAXBUnmarshalException jaxbE = new JAXBUnmarshalException("JAXBUnmarshalException's ReaderException test",
                Response.status(ReaderExceptionMapper.STATUS_CODE)
                        .entity("JAXBUnmarshalException response").build());

        factory.registerProvider(ReaderExceptionMapper.class);
        Response result = eHandler.handleException(request, jaxbE);

        Assertions.assertEquals(result.getStatus(), ReaderExceptionMapper.STATUS_CODE,
                "ReaderExceptionMapper: incorrect status code returned");
    }

}
