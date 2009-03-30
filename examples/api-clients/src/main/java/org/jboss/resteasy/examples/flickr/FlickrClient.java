package org.jboss.resteasy.examples.flickr;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Date;
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
import org.jboss.resteasy.client.cache.BrowserCache;
import org.jboss.resteasy.client.cache.CacheFactory;
import org.jboss.resteasy.client.cache.LightweightBrowserCache;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

public class FlickrClient {
	static final String photoSearchUrl = "http://www.flickr.com/services/rest?method=flickr.photos.search&per_page=10&sort=interestingness-desc";
	static final String photoServer = "http://static.flickr.com";
	static final String photoPath = "/{server}/{id}_{secret}_m.jpg";

	static JFrame frame = null;
	static JPanel dataPanel = null;
	static String apiKey = null;
	static BrowserCache cache = new LightweightBrowserCache();

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
		apiKey = args[0];
		createInitialFrame();
	}

	private static void createInitialFrame() {
		frame = new JFrame();
		frame.setTitle("Flickr Search");
		frame.setLayout(new BorderLayout());
		frame.add(createQueryPanel(), BorderLayout.NORTH);
		frame.add(dataPanel = new JPanel(), BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.pack();
		frame.setSize(frame.getSize().width + 400, frame.getSize().height);
		frame.setVisible(true);
	}

	private static Rsp getPhotos(final String searchTerm) throws Exception {
		ClientRequest request = new ClientRequest(photoSearchUrl)
				.queryParameter("api key", apiKey).queryParameter("tags",
						searchTerm);
		CacheFactory.makeCacheable(request, cache);
		Rsp photos = request.get(Rsp.class).getEntity();
		photos.searchTerm = searchTerm;
		return photos;
	}

	private static JPanel createQueryPanel() {
		JPanel queryPanel = new JPanel(new BorderLayout());
		queryPanel.add(new JLabel("Query:"), BorderLayout.WEST);
		final JTextField textField = new JTextField();
		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					updatePhotos(textField.getText());
				}
			}
		});
		queryPanel.add(textField, BorderLayout.CENTER);
		JButton searchButton = new JButton("Search");
		searchButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				updatePhotos(textField.getText());
			}
		});
		queryPanel.add(searchButton, BorderLayout.EAST);
		return queryPanel;
	}

	private static void updatePhotos(String searchTerm) {
		try {
			frame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			System.out.println(new Date() + " search for " + searchTerm);
			Rsp photos = getPhotos(searchTerm);
			System.out.println(new Date() + " got " + photos.photo.size() + " results for " + searchTerm);
			displayPhotos(photos);
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			System.out.println(new Date() + " finished search for " + searchTerm);
			frame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
	}

	private static void displayPhotos(Rsp photos) throws MalformedURLException,
			Exception {
		frame.setTitle("Flickr Search for " + photos.searchTerm);
		if(dataPanel != null)
		frame.remove(dataPanel);
		dataPanel=new JPanel();
		dataPanel.setLayout(new GridLayout(2, photos.photo.size() / 2));
		
		for (Photo photo : photos.photo) {
			JPanel panel = new JPanel(new BorderLayout());
			System.out.println(new Date() + " reading photo " + photo.getURI());
			
			panel.add(new JLabel(photo.title), BorderLayout.NORTH);
			panel.add(new JLabel(new ImageIcon(photo.getURI().toURL())),
					BorderLayout.CENTER);
			dataPanel.add(panel);
		}
		frame.add(dataPanel, BorderLayout.CENTER);
		frame.pack();
	}
}
