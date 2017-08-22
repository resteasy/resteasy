package org.jboss.resteasy.test.resource.param.resource;

import javax.ws.rs.ext.ParamConverter;

public class MultiValuedPathParamConverter implements ParamConverter<MultiValuedPathParam<?>> {

	private final ParamConverter<?> paramConverter;

	public MultiValuedPathParamConverter(ParamConverter<?> paramConverter) {
		this.paramConverter = paramConverter;
	}

	@Override
	public MultiValuedPathParam<?> fromString(String param) {
		if (param == null || param.trim().isEmpty()) {
			return null;
		}
		return parse(param.split("/"), this.paramConverter);
	}

	@Override
	public String toString(MultiValuedPathParam<?> multiValuedPathParam) {
		if (multiValuedPathParam == null || multiValuedPathParam.isEmpty()) {
			return null;
		}
		return format(multiValuedPathParam, this.paramConverter);
	}

	private static <T> MultiValuedPathParam<T> parse(String[] params, ParamConverter<T> paramConverter) {
		MultiValuedPathParam<T> multiValuedPathParam = new MultiValuedPathParam<>();
		for (String param : params) {
			multiValuedPathParam.add(paramConverter.fromString(param));
		}
		return multiValuedPathParam;
	}

	@SuppressWarnings("unchecked")
	private static <T> String format(MultiValuedPathParam<?> multiValuedPathParam,
			ParamConverter<T> paramConverter) {
		StringBuilder stringBuilder = new StringBuilder();
		int size = multiValuedPathParam.size();
		for (int i = 0; i < size; i++) {
			stringBuilder.append(paramConverter.toString((T) multiValuedPathParam.get(i)));
			if (i != size - 1) {
				stringBuilder.append('/');
			}
		}
		return stringBuilder.toString();
	}

}
