package org.jboss.resteasy.test.cache;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.io.OutputStream;
import java.net.URI;

import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Request;
import javax.ws.rs.ext.WriterInterceptorContext;

import org.jboss.resteasy.plugins.cache.server.ServerCache;
import org.jboss.resteasy.plugins.cache.server.ServerCacheInterceptor;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.ResteasyUriInfo;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for RESTEASY-1423
 * 
 * @author <a href="mailto:dxtr66@gmail.com">Matthias Poell</a>
 * 
 */
public class ServerCacheInterceptorTest {

	private ServerCache cache;
	private CacheControl cc;
	private TestServerCacheInterceptor interceptor;
	private WriterInterceptorContext context;
	private HttpRequest request;


	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Before
	public void setupServerCacheInterceptor() {
		cache = mock(ServerCache.class);
		context = mock(WriterInterceptorContext.class);
		cc = new CacheControl();

		request = mock(HttpRequest.class);
		MultivaluedMap headers = mock(MultivaluedMap.class);
		when(headers.getFirst(HttpHeaders.CACHE_CONTROL)).thenReturn(cc);
		when(request.getHttpMethod()).thenReturn("GET");
		when(request.getUri()).thenReturn(mock(ResteasyUriInfo.class));
		when(context.getHeaders()).thenReturn(headers);
		when(context.getOutputStream()).thenReturn(mock(OutputStream.class));
		
		interceptor = new TestServerCacheInterceptor(cache);
		interceptor.setHttpRequest(request);
		
	}
	
	/**
	 * Verifies that if the cache control header field 'private' is set, nothing is added to the cache. 
	 */
	@Test
	public void respectCacheControlPrivateFlag() throws Exception {
		cc.setPrivate(true);
		interceptor.aroundWriteTo(context);
		verifyZeroInteractions(cache);
	}

	/**
	 * Verifies that if the cache control header field 'no-store' is set, nothing is added to the cache. 
	 */
	@Test
	public void respectCacheControlNoStoreFlag() throws Exception {
		cc.setNoStore(true);
		interceptor.aroundWriteTo(context);
		verifyZeroInteractions(cache);
	}

	/**
	 * Verifies that if neither of the the cache control header fields 'no-store' and 'private' are present, the response is added to the cache. 
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void addResponseToCacheIfPrivateAndNoStoreFlagsAreNotSet() throws Exception {
		URI uri = new URI("someuri");
		when(request.getUri().getRequestUri()).thenReturn(uri);
		cc.setPrivate(false);
		cc.setNoStore(false);
		interceptor.aroundWriteTo(context);
		verify(cache).add(eq(uri.toString()), any(MediaType.class), eq(cc), any(MultivaluedMap.class), any(byte[].class), anyString());
	}

	/**
	 * Subclass of ServerCacheInterceptor to make it "unit-test able"
	 */
	private class TestServerCacheInterceptor extends ServerCacheInterceptor {

		public TestServerCacheInterceptor(ServerCache cache) {
			super(cache);
			validation = mock(Request.class);
		}
		
		public void setHttpRequest(HttpRequest request) {
			this.request = request;
		}
		
	}
}
