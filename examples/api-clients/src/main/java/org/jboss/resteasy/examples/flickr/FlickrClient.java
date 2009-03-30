package org.jboss.resteasy.examples.flickr;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

public class FlickrClient {
	@XmlRootElement()
	public static class Rsp {
		@XmlElementWrapper(name = "photos")
		public List<Photo> photo;
	}

	public static class Photo {
		final static String photoUrl = "http://static.flickr.com/%s/%s_%s_m.jpg";
		@XmlAttribute
		public String server, id, secret, title;

		public URL getURL() throws Exception {
			return new URL(String.format(photoUrl, server, id, secret));
		}
	}

	public static void main(String args[]) throws Exception {
		final String photoSearchUrl = "http://www.flickr.com/services/rest?method=flickr.photos.search&per_page=10&sort=interestingness-desc";
		final String searchTerm = getString(Arrays.asList(args).subList(1, args.length));

		RegisterBuiltin.register(ResteasyProviderFactory.getInstance());

		// apply for api key at - http://www.flickr.com/services/api/keys/apply
		ClientRequest request = new ClientRequest(photoSearchUrl)
			.queryParameter("api key", args[0])
			.queryParameter("tags", searchTerm);
		Rsp photos = request.get(Rsp.class).getEntity();

		JFrame frame = new JFrame(searchTerm + " photos");
		frame.setLayout(new GridLayout(2, photos.photo.size() / 2));
		for (Photo photo : photos.photo) {
			JPanel panel = new JPanel(new BorderLayout());
			panel.add(new JLabel(photo.title), BorderLayout.NORTH);
			panel.add(new JLabel(new ImageIcon(photo.getURL())), BorderLayout.CENTER);
			frame.add(panel);
		}
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}

	private static String getString(List<String> list) {
		StringBuilder sb = new StringBuilder();
		String append = "";
		for (String term : list) {
			sb.append(append).append(term);
			append = " ";
		}
		return sb.toString();
	}
}
