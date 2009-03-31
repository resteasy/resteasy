package org.jboss.resteasy.examples.flickr;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import javax.swing.ImageIcon;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.client.cache.BrowserCache;
import org.jboss.resteasy.client.cache.CacheFactory;
import org.jboss.resteasy.client.cache.CacheInterceptor;
import org.jboss.resteasy.client.core.BaseClientResponse;
import org.jboss.resteasy.core.interception.ClientExecutionInterceptor;
import org.jboss.resteasy.util.DateUtil;
import org.jboss.resteasy.util.HttpHeaderNames;

public class FlickrSearchService {
	static final String photoSearchUrl = "http://www.flickr.com/services/rest";
	static final String photoServer = "http://static.flickr.com";
	static final String photoPath = "/{server}/{id}_{secret}_m.jpg";

	String apiKey;
	PhotoResource photoResource;
	BrowserCache cache;
	HttpClient client = null;
	Executor executor = Executors.newFixedThreadPool(10);

	static interface PhotoResource {
		@GET
		@Path(photoPath)
		ImageIcon read(@PathParam("server") String server,
				@PathParam("id") String id, @PathParam("secret") String secret);
	}

	public FlickrSearchService(String apiKey, BrowserCache browserCache) {
		this.apiKey = apiKey;
		this.cache = browserCache;
		client = new HttpClient(new MultiThreadedHttpConnectionManager()) {
			@Override
			public int executeMethod(HttpMethod method) throws IOException,
					HttpException {
				System.out.println(new Date() + " reading " + method.getURI());
				return super.executeMethod(method);
			}
		};
		
		// create a proxy using the JAX-RS annotation
		photoResource = ProxyFactory.create(PhotoResource.class, photoServer, client);
		// add caching to the proxy
		CacheFactory.makeCacheable(photoResource, cache);
	}

	public FlickrResponse searchPhotos(String type, String searchTerm)
			throws Exception {
		ClientRequest request = new ClientRequest(photoSearchUrl, client)
				.queryParameter("method", "flickr.photos.search")
				.queryParameter("per_page", 8).queryParameter("sort",
						"interestingness-desc").queryParameter("api key",
						apiKey).queryParameter(type, searchTerm);
		
		request.setExecutionInterceptors(getForcedCachingInterceptors());
		
		System.out.println(new Date() + " search for " + searchTerm);

		// first, print out the raw XML
		System.out.println(request.get(String.class).getEntity());
		
		// second, convert the XML to a JAXB object
		FlickrResponse photos = request.get(FlickrResponse.class).getEntity();
		prefetchImages(photos);
		return photos;
	}

	private ClientExecutionInterceptor[] getForcedCachingInterceptors() {
		// I wouldn't recommend this everywhere... This overrides Flickr's
		// Caching behavior to cache for 10 minutes
		@SuppressWarnings("unchecked")
		CacheInterceptor interceptor = new CacheInterceptor(cache) {
			@Override
			public ClientResponse cacheIfPossible(ClientRequest request,
					BaseClientResponse response) throws Exception {
				setExpiredToTheFuture(response);
				return super.cacheIfPossible(request, response);
			}

			private void setExpiredToTheFuture(BaseClientResponse response) {
				MultivaluedMap headers = response.getHeaders();
				Object date = headers.getFirst(HttpHeaderNames.DATE);
				if (date != null && headers.getFirst(HttpHeaderNames.EXPIRES) == null) {
					String later = DateUtil.formatDate(add10Minutes(date));
					headers.add(HttpHeaderNames.EXPIRES, later);
				}
			}

			private Date add10Minutes(Object date) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(DateUtil.parseDate(date.toString()));
				cal.add(Calendar.MINUTE, 10);
				return cal.getTime();
			}
		};
		ClientExecutionInterceptor[] interceptors = new ClientExecutionInterceptor[] { interceptor };
		return interceptors;
	}

	private void prefetchImages(FlickrResponse photos) {
		for (final Photo photo : photos.photo) {
			photo.image = new FutureTask<ImageIcon>(new Callable<ImageIcon>() {
				public ImageIcon call() throws Exception {
					return photoResource.read(photo.server, photo.id,
							photo.secret);
				}
			});
			executor.execute(photo.image);
		}
	}

	public ImageIcon getImageIcon(Photo photo) throws Exception {
		return photo.image.get();
	}
}
