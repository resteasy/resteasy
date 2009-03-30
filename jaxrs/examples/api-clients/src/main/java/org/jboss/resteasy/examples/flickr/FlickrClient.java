package org.jboss.resteasy.examples.flickr;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Iterator;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

public class FlickrClient {
	static final String photoSearchUrl = "http://www.flickr.com/services/rest?method=flickr.photos.search&per_page=10&sort=interestingness-desc";
	static final String photoServer = "http://static.flickr.com";
	static final String photoPath = "/{server}/{id}_{secret}_m.jpg";

	static JFrame frame = null;
	static JPanel dataPanel = null;

	@XmlRootElement()
	public static class Rsp {
		@XmlElementWrapper(name = "photos")
		public List<Photo> photo;
		@XmlTransient
		public String searchTerm;
	}

	public static class Photo {
		@XmlAttribute
		public String server, id, secret, title;

		public URI getURI() throws Exception {
			return UriBuilder.fromUri(photoServer).path(photoPath).build(
					server, id, secret);
		}
	}

	public static void main(String args[]) throws Exception {
		RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
		createInitialFrame(args[0]);
	}

	private static void createInitialFrame(final String apiKey) {
		frame = new JFrame();
		frame.setTitle("Flickr Search");
		frame.setLayout(new BorderLayout());
		dataPanel = new JPanel();
		JPanel queryPanel = createQueryPanel(apiKey);
		frame.add(queryPanel, BorderLayout.NORTH);
		frame.add(dataPanel, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.pack();
		frame.setSize(frame.getSize().width + 400, frame.getSize().height);
		frame.setVisible(true);
	}

	private static Rsp getPhotos(String apiKey, final String searchTerm)
			throws Exception {
		Rsp photos = new ClientRequest(photoSearchUrl).queryParameter(
				"api key", apiKey).queryParameter("tags", searchTerm).get(
				Rsp.class).getEntity();
		photos.searchTerm = searchTerm;
		return photos;
	}

	private static JPanel createQueryPanel(final String apiKey) {
		JPanel queryPanel = new JPanel(new BorderLayout());
		queryPanel.add(new JLabel("Query:"), BorderLayout.WEST);
		final JTextField textField = new JTextField();
		queryPanel.add(textField, BorderLayout.CENTER);
		JButton searchButton = new JButton("Search");
		searchButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					displayPhotos(getPhotos(apiKey, textField.getText()));
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		queryPanel.add(searchButton, BorderLayout.EAST);
		return queryPanel;
	}

	private static void displayPhotos(Rsp photos) throws MalformedURLException,
			Exception {
		frame.setTitle("Flickr Search for " + photos.searchTerm);
		dataPanel.removeAll();
		dataPanel.setLayout(new GridLayout(2, photos.photo.size() / 2));
		for (Photo photo : photos.photo) {
			JPanel panel = new JPanel(new BorderLayout());
			panel.add(new JLabel(photo.title), BorderLayout.NORTH);
			panel.add(new JLabel(new ImageIcon(photo.getURI().toURL())),
					BorderLayout.CENTER);
			dataPanel.add(panel);
		}
		frame.pack();
	}
}
