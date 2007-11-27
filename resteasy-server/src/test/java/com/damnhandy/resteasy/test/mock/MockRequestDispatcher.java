package com.damnhandy.resteasy.test.mock;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Mock implementation of the {@link javax.servlet.RequestDispatcher} interface.
 *
 * <p>Used for testing the web framework; typically not necessary for
 * testing application controllers.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 1.0.2
 */
public class MockRequestDispatcher implements RequestDispatcher {


	private final String url;


	/**
	 * Create a new MockRequestDispatcher for the given URL.
	 * @param url the URL to dispatch to.
	 */
	public MockRequestDispatcher(String url) {
		Assert.notNull(url, "URL must not be null");
		this.url = url;
	}


	public void forward(ServletRequest request, ServletResponse response) {
		Assert.notNull(request, "Request must not be null");
		Assert.notNull(response, "Response must not be null");
		if (response.isCommitted()) {
			throw new IllegalStateException("Cannot perform forward - response is already committed");
		}
		if (!(response instanceof MockHttpServletResponse)) {
			throw new IllegalArgumentException("MockRequestDispatcher requires MockHttpServletResponse");
		}
		((MockHttpServletResponse) response).setForwardedUrl(this.url);
	}

	public void include(ServletRequest request, ServletResponse response) {
		Assert.notNull(request, "Request must not be null");
		Assert.notNull(response, "Response must not be null");
		if (!(response instanceof MockHttpServletResponse)) {
			throw new IllegalArgumentException("MockRequestDispatcher requires MockHttpServletResponse");
		}
		((MockHttpServletResponse) response).setIncludedUrl(this.url);
	}

}
