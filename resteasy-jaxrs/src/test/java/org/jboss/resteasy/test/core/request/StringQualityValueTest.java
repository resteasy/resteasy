package org.jboss.resteasy.test.core.request;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
public class StringQualityValueTest {

	@Test
	public void simple() {
		String[] fields = {"compress", "gzip"};
		QualityValue[] qualities = {
				QualityValue.DEFAULT,
				QualityValue.DEFAULT
		};
		assertList("compress, gzip", fields, qualities);
		assertList(" compress,gzip ", fields, qualities);
		assertList("compress ,gzip", fields, qualities);
	}


	@Test
	public void parameter() {
		String header = "iso-8859-5, unicode-1-1;q=0.8";
		String[] fields = {"iso-8859-5", "unicode-1-1"};
		QualityValue[] qualities = {
				QualityValue.DEFAULT,
				QualityValue.valueOf("0.8")
		};
		assertList(header, fields, qualities);
	}


	@Test
	public void wildcard() {
		String header = "*";
		String[] fields = {null};
		QualityValue[] qualities = {QualityValue.DEFAULT};
		assertList(header, fields, qualities);
	}


	@Test
	public void wildcardWithParameter() {
		String header = "gzip;q=1.0, identity; q=0.5, *;q=0";
		String[] fields = {"gzip", "identity", null};
		QualityValue[] qualities = {
				QualityValue.valueOf("1.0"),
				QualityValue.valueOf("0.5"),
				QualityValue.valueOf("0")
		};
		assertList(header, fields, qualities);
	}


	@Test
	public void badRequests() {
		String[] badHeaders = {
				" ,b,c",		// empty fields
				"a, ,c",
				"a,b, ",
				",",
				"a;",			// empty parameters
				"a;,b",
				"a;x=0",		// illegal parameters
				"a;q=0.1;q=0.1",
				"a;illegal"
		};
		for (String header : badHeaders) {
			try {
				AcceptHeaders.getStringQualityValues(header);
				fail(header);
			} catch (BadRequestException e) {
			}
		}
	}


	@Test
	public void empty() {
		assertNull(AcceptHeaders.getStringQualityValues(null));
		assertNull(AcceptHeaders.getStringQualityValues(""));
		assertNull(AcceptHeaders.getStringQualityValues(" "));
	}


	private static void assertList(String header, String[] fields, QualityValue[] qualities) {
		Map<String,QualityValue> map = AcceptHeaders.getStringQualityValues(header);
		List<String> expectedKeys = Arrays.asList(fields);
		List<QualityValue> expectedValues = Arrays.asList(qualities);
		assertEquals(expectedKeys, new ArrayList<String>(map.keySet()));
		assertEquals(expectedValues, new ArrayList<QualityValue>(map.values()));
	}

}
