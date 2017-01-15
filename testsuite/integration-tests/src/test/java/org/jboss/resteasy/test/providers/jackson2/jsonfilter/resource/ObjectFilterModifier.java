package org.jboss.resteasy.test.providers.jackson2.jsonfilter.resource;

import java.io.IOException;

import javax.ws.rs.core.MultivaluedMap;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.jaxrs.cfg.EndpointConfigBase;
import com.fasterxml.jackson.jaxrs.cfg.ObjectWriterModifier;
/**
 * @author <a href="mailto:ema@redhat.com">Jim Ma</a>
 *
 */
public class ObjectFilterModifier extends ObjectWriterModifier {
	public ObjectFilterModifier() {
	}
	@Override
	public ObjectWriter modify(EndpointConfigBase<?> endpoint,
			MultivaluedMap<String, Object> httpHeaders, Object valueToWrite,
			ObjectWriter w, JsonGenerator jg) throws IOException {

		FilterProvider filterProvider = new SimpleFilterProvider().addFilter(
				"nameFilter",
				SimpleBeanPropertyFilter.filterOutAllExcept("name"));
		return w.with(filterProvider);

	}
}
