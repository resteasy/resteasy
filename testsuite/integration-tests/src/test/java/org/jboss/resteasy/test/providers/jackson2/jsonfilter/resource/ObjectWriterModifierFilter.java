package org.jboss.resteasy.test.providers.jackson2.jsonfilter.resource;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.fasterxml.jackson.jaxrs.cfg.ObjectWriterInjector;

/**
 * @author <a href="mailto:ema@redhat.com">Jim Ma</a>
 *
 */
public class ObjectWriterModifierFilter implements Filter {
	private static ObjectFilterModifier modifier = new ObjectFilterModifier();

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		ObjectWriterInjector.set(modifier);
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
	}

}
