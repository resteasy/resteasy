package org.jboss.resteasy.examples.resteasy;

import java.util.Date;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.lang.time.DateUtils;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.core.interception.ClientExecutionContext;
import org.jboss.resteasy.core.interception.ClientExecutionInterceptor;
import org.jboss.resteasy.util.DateUtil;
import org.jboss.resteasy.util.HttpHeaderNames;

public class ForceCachingExecutionInterceptor implements
		ClientExecutionInterceptor
{
	private int minutes;

	public ForceCachingExecutionInterceptor(int minutes)
	{
		this.minutes = minutes;
	}

	@SuppressWarnings("unchecked")
	public ClientResponse execute(ClientExecutionContext ctx) throws Exception
	{
		ClientResponse resp = ctx.proceed();
		MultivaluedMap<String, String> headers = resp.getHeaders();
		String date = headers.getFirst(HttpHeaderNames.DATE);
		if (date != null && headers.getFirst(HttpHeaderNames.EXPIRES) == null)
		{
			Date future = DateUtils.addMinutes(DateUtil.parseDate(date), minutes);
			headers.add(HttpHeaderNames.EXPIRES, DateUtil.formatDate(future));
		}
		return resp;
	}
}
