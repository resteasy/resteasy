package org.jboss.resteasy.test.resource.param.resource;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ext.ParamConverter;

public class DateParamConverter implements ParamConverter<Date> {

	public static final String DATE_PATTERN = "yyyyMMdd";

	@Override
	public Date fromString(String param) {
		try {
			return new SimpleDateFormat(DATE_PATTERN).parse(param.trim());
		} catch (ParseException e) {
			throw new BadRequestException(e);
		}
	}

	@Override
	public String toString(Date date) {
		return new SimpleDateFormat(DATE_PATTERN).format(date);
	}

}
