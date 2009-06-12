package org.jboss.resteasy.examples.flickr;

import static org.jboss.resteasy.examples.flickr.FlickrConstants.photoSearchUrl;
import static org.jboss.resteasy.examples.flickr.FlickrConstants.photoUrlTemplate;

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.jboss.resteasy.client.ClientRequestFactory;
import org.jboss.resteasy.examples.resteasy.ImageIconMessageBodyReader;
import org.jboss.resteasy.examples.resteasy.LoggingExecutionInterceptor;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

public class SimpleFlickrClient
{
    public static void main(String args[]) throws Exception
    {
        final String searchTerm = "dolphin";

        ClientRequestFactory client = initializeRequests();

        // apply for api key at - http://www.flickr.com/services/api/keys/apply
        FlickrResponse photos = client.get(photoSearchUrl,
                FlickrResponse.class, args[0], "text", searchTerm);

        JFrame frame = new JFrame(searchTerm + " photos");
        frame.setLayout(new GridLayout(2, photos.photos.size() / 2));

        for (Photo photo : photos.photos)
        { 
            JLabel image = new JLabel(client.get(photoUrlTemplate,
                    ImageIcon.class, photo.server, photo.id, photo.secret));
            image.setBorder(BorderFactory.createTitledBorder(photo.title));
            frame.add(image);
        }

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private static ClientRequestFactory initializeRequests()
    {
        ResteasyProviderFactory instance = ResteasyProviderFactory.getInstance();
        RegisterBuiltin.register(instance);
        instance.registerProvider(ImageIconMessageBodyReader.class);

        ClientRequestFactory client = new ClientRequestFactory();
        client.getPrefixInterceptors().registerInterceptor(new LoggingExecutionInterceptor());
        return client;
    }
}
