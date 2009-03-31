package org.jboss.resteasy.examples.flickr;

import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import javax.swing.ImageIcon;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.client.cache.BrowserCache;
import org.jboss.resteasy.client.cache.CacheFactory;

@Path("/services/rest")
public class FlickrSearchService {
	static final String photoSearchUrl = "http://www.flickr.com/services/rest?method=flickr.photos.search&per_page=8&sort=interestingness-desc";
	static final String photoServer = "http://static.flickr.com";
	static final String photoPath = "/{server}/{id}_{secret}_m.jpg";

	String apiKey;
	PhotoResource photoResource;
	BrowserCache cache;
	HttpClient client = new HttpClient(new MultiThreadedHttpConnectionManager());
	Executor executor = Executors.newFixedThreadPool(10);

	@Path(photoPath)
	static interface PhotoResource {
		@GET
		ImageIcon read(@PathParam("server") String server,
				@PathParam("id") String id, @PathParam("secret") String secret);
	}

	public FlickrSearchService(String apiKey, BrowserCache browserCache) {
		this.apiKey = apiKey;
		this.cache = browserCache;
		photoResource = ProxyFactory.create(PhotoResource.class, photoServer,
				client);
		CacheFactory.makeCacheable(photoResource, cache);
	}

	public FlickrResponse searchPhotos(String type, String searchTerm)
			throws Exception {
		ClientRequest request = new ClientRequest(photoSearchUrl, client)
				.queryParameter("api key", apiKey).queryParameter(type,
						searchTerm);
		CacheFactory.makeCacheable(request, cache);

		System.out.println(new Date() + " search for " + searchTerm);

		FlickrResponse photos = request.get(FlickrResponse.class).getEntity();

		for (final Photo photo : photos.photo) {
			photo.image = new FutureTask<ImageIcon>(new Callable<ImageIcon>() {
				public ImageIcon call() throws Exception {
					try{
						System.out.println(new Date() + " reading " + photo.id);
						return photoResource.read(photo.server, photo.id,
							photo.secret);
					}finally{
						synchronized (FlickrSearchService.this) {
							FlickrSearchService.this.notifyAll();
						}
						System.out.println(new Date() + " done reading " + photo.id);
					}
				}
			});
			executor.execute(photo.image);
		}
		System.out.println(new Date() + " got " + photos.photo.size()
				+ " results for " + searchTerm);

		photos.searchTerm = searchTerm;
		return photos;
	}

	public synchronized ImageIcon getImageIcon(Photo photo) throws Exception {
		// try every 10 seconds
		while (!photo.image.isDone()) {
			try {
				wait(1000);
			} catch (InterruptedException ie) {
			}
		}
		return photo.image.get();

	}
}
