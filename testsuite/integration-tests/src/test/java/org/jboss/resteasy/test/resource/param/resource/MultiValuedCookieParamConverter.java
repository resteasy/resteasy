package org.jboss.resteasy.test.resource.param.resource;

import javax.ws.rs.ext.ParamConverter;

public class MultiValuedCookieParamConverter implements ParamConverter<MultiValuedCookieParam<?>> {

	private final ParamConverter<?> paramConverter;

	public MultiValuedCookieParamConverter(ParamConverter<?> paramConverter) {
		this.paramConverter = paramConverter;
	}

	@Override
	public MultiValuedCookieParam<?> fromString(String param) {
		if (param == null || param.trim().isEmpty()) {
			return null;
		}
		return parse(param.split("-"), this.paramConverter);
	}

	@Override
	public String toString(MultiValuedCookieParam<?> multiValuedCookieParam) {
		if (multiValuedCookieParam == null || multiValuedCookieParam.isEmpty()) {
			return null;
		}
		return format(multiValuedCookieParam, this.paramConverter);
	}

	private static <T> MultiValuedCookieParam<T> parse(String[] params, ParamConverter<T> paramConverter) {
		MultiValuedCookieParam<T> multiValuedCookieParam = new MultiValuedCookieParam<>();
		for (String param : params) {
			multiValuedCookieParam.add(paramConverter.fromString(param));
		}
		return multiValuedCookieParam;
	}

	@SuppressWarnings("unchecked")
	private static <T> String format(MultiValuedCookieParam<?> multiValuedCookieParam,
			ParamConverter<T> paramConverter) {
		StringBuilder stringBuilder = new StringBuilder();
		int size = multiValuedCookieParam.size();
		for (int i = 0; i < size; i++) {
			stringBuilder.append(paramConverter.toString((T) multiValuedCookieParam.get(i)));
			if (i != size - 1) {
				stringBuilder.append('-');
			}
		}
		return stringBuilder.toString();
	}

}
