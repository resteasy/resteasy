package org.jboss.resteasy.test.resource.param.resource;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.ext.ParamConverter;

public class DateListParamConverter implements ParamConverter<LinkedList<Date>> {

	@Override
	public LinkedList<Date> fromString(String param) {
		if (param == null || param.trim().isEmpty()) {
			return null;
		}
		return parse(param.split(","), new DateParamConverter());
	}

	@Override
	public String toString(LinkedList<Date> list) {
		if (list == null || list.isEmpty()) {
			return null;
		}
		return format(list, new DateParamConverter());
	}

	private static <T> LinkedList<T> parse(String[] params, ParamConverter<T> paramConverter) {
		LinkedList<T> list = new LinkedList<>();
		for (String param : params) {
			list.add(paramConverter.fromString(param));
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	private static <T> String format(List<?> list, ParamConverter<T> paramConverter) {
		StringBuilder stringBuilder = new StringBuilder();
		int size = list.size();
		for (int i = 0; i < size; i++) {
			stringBuilder.append(paramConverter.toString((T) list.get(i)));
			if (i != size - 1) {
				stringBuilder.append(',');
			}
		}
		return stringBuilder.toString();
	}

}
