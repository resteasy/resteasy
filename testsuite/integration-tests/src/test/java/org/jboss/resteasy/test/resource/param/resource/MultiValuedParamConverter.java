package org.jboss.resteasy.test.resource.param.resource;

import javax.ws.rs.ext.ParamConverter;

public class MultiValuedParamConverter implements ParamConverter<MultiValuedParam<?>> {

	private final ParamConverter<?> paramConverter;

	public MultiValuedParamConverter(ParamConverter<?> paramConverter) {
		this.paramConverter = paramConverter;
	}

	@Override
	public MultiValuedParam<?> fromString(String param) {
		if (param == null || param.trim().isEmpty()) {
			return null;
		}
		return parse(param.split(","), this.paramConverter);
	}

	@Override
	public String toString(MultiValuedParam<?> multiValuedParam) {
		if (multiValuedParam == null || multiValuedParam.isEmpty()) {
			return null;
		}
		return format(multiValuedParam, this.paramConverter);
	}

	private static <T> MultiValuedParam<T> parse(String[] params, ParamConverter<T> paramConverter) {
		MultiValuedParam<T> multiValuedParam = new MultiValuedParam<>();
		for (String param : params) {
			multiValuedParam.add(paramConverter.fromString(param));
		}
		return multiValuedParam;
	}

	@SuppressWarnings("unchecked")
	private static <T> String format(MultiValuedParam<?> multiValuedParam, ParamConverter<T> paramConverter) {
		StringBuilder stringBuilder = new StringBuilder();
		int size = multiValuedParam.size();
		for (int i = 0; i < size; i++) {
			stringBuilder.append(paramConverter.toString((T) multiValuedParam.get(i)));
			if (i != size - 1) {
				stringBuilder.append(',');
			}
		}
		return stringBuilder.toString();
	}

}
