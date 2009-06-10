package org.jboss.resteasy.examples.flickr;

import static org.jboss.resteasy.examples.flickr.FlickrConstants.photoPath;
import static org.jboss.resteasy.examples.flickr.FlickrConstants.photoSearchUrl;
import static org.jboss.resteasy.examples.flickr.FlickrConstants.photoServer;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.ImageIcon;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.jboss.resteasy.client.ClientRequestFactory;
import org.jboss.resteasy.client.cache.CacheInterceptor;
import org.jboss.resteasy.client.cache.LightweightBrowserCache;
import org.jboss.resteasy.client.core.ClientInterceptorRepositoryImpl;
import org.jboss.resteasy.client.core.executors.ApacheHttpClientExecutor;
import org.jboss.resteasy.examples.resteasy.ForceCachingExecutionInterceptor;
import org.jboss.resteasy.examples.resteasy.LoggingExecutionInterceptor;

public class FlickrSearchService
{

    private String apiKey;
    private PhotoResource photoResource;
    private ExecutorService executor = Executors.newFixedThreadPool(8);
    private ClientRequestFactory clientRequestFactory;

    @Path("")
    static interface PhotoResource
    {
        @GET
        @Path(photoPath)
        ImageIcon read(@PathParam("server") String server,
                @PathParam("id") String id, @PathParam("secret") String secret);
    }

    public FlickrSearchService(String apiKey) throws URISyntaxException
    {
        this.apiKey = apiKey;
        clientRequestFactory = new ClientRequestFactory(
                new ApacheHttpClientExecutor(new HttpClient(
                        new MultiThreadedHttpConnectionManager())), new URI(
                        photoServer));

        ClientInterceptorRepositoryImpl interceptors = clientRequestFactory
                .getPrefixInterceptors();
        interceptors.registerInterceptor(new LoggingExecutionInterceptor());
        interceptors.registerInterceptor(new CacheInterceptor(
                new LightweightBrowserCache()));
        interceptors.registerInterceptor(new ForceCachingExecutionInterceptor(
                10));

        // create a proxy using the JAX-RS annotation
        photoResource = clientRequestFactory.createProxy(PhotoResource.class);
    }

    public FlickrResponse searchPhotos(String type, String searchTerm)
            throws Exception
    {
        // second, convert the XML to a JAXB object
        FlickrResponse photos = clientRequestFactory.get(photoSearchUrl,
                FlickrResponse.class, apiKey, type, searchTerm);
        prefetchImages(photos);
        return photos;
    }

    private void prefetchImages(FlickrResponse photos)
    {
        for (final Photo photo : photos.photos)
        {
            photo.image = executor.submit(new Callable<ImageIcon>()
            {
                public ImageIcon call() throws Exception
                {
                    return photoResource.read(photo.server, photo.id,
                            photo.secret);
                }
            });
        }
    }

    public ImageIcon getImageIcon(Photo photo) throws Exception
    {
        return photo.image.get();
    }
}
