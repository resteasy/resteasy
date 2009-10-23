package org.jboss.resteasy.test.core.request;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import org.junit.Test;

import org.jboss.resteasy.core.request.AcceptHeaders;
import org.jboss.resteasy.core.request.QualityValue;
import org.jboss.resteasy.spi.BadRequestException;


/**
 @author Pascal S. de Kloe
 */
public class MediaTypeQualityValueTest {

	@Test
	public void simple() {
		String header = "audio/*; q=0.2, audio/basic";
		MediaType[] fields = {
				MediaType.valueOf("audio/*"),
				MediaType.valueOf("audio/basic")
		};
		QualityValue[] qualities = {
				QualityValue.valueOf("0.2"),
				QualityValue.DEFAULT
		};
		assertList(header, fields, qualities);
	}


	@Test
	public void parameters() {
		String header = "text/html;level=\"1\", text/html;level=2;q=0.4";
		MediaType[] fields = {
				MediaType.valueOf("text/html;level=1"),
				MediaType.valueOf("text/html;level=2")
		};
		QualityValue[] qualities = {
				QualityValue.DEFAULT,
				QualityValue.valueOf("0.4")
		};
		assertList(header, fields, qualities);
	}


	@Test
	public void unsupportedExtension() {
		String header = "plain/text; a=b; q=0.2; extension=unsupported";
		MediaType[] fields = {MediaType.valueOf("plain/text;a=b")};
		QualityValue[] qualities = {QualityValue.NOT_ACCEPTABLE};
		assertList(header, fields, qualities);
	}


	@Test
	public void badRequests() {
		String[] badHeaders = {
				"a",
				"a,b",
				"a/b,",
				"a/b;",
				"a/b;p",
				"a/b;p=x,",
				"a/b;p=\"x\"y",
				"a/b;p=\"x\"y,c/d",
				"a/b;p=\"x,c/d",
				"a/b;p=\"x\\\",c/d"
		};
		for (String header : badHeaders) {
			try {
				AcceptHeaders.getMediaTypeQualityValues(header);
				fail(header);
			} catch (BadRequestException e) {
			}
		}
	}


	@Test
	public void empty() {
		assertNull(AcceptHeaders.getMediaTypeQualityValues(null));
		assertNull(AcceptHeaders.getMediaTypeQualityValues(""));
		assertNull(AcceptHeaders.getMediaTypeQualityValues(" "));
	}


	private static void assertList(String header, MediaType[] fields, QualityValue[] qualities) {
		Map<MediaType,QualityValue> map = AcceptHeaders.getMediaTypeQualityValues(header);
		List<MediaType> expectedKeys = Arrays.asList(fields);
		List<QualityValue> expectedValues = Arrays.asList(qualities);
		assertEquals(expectedKeys, new ArrayList<MediaType>(map.keySet()));
		assertEquals(expectedValues, new ArrayList<QualityValue>(map.values()));
	}

}
