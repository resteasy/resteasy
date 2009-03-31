package org.jboss.resteasy.examples.flickr;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.synth.SynthLookAndFeel;

import org.jboss.resteasy.client.cache.LightweightBrowserCache;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

public class FlickrClient {

	public static void main(String args[]) throws Exception {
		ResteasyProviderFactory instance = ResteasyProviderFactory
				.getInstance();
		RegisterBuiltin.register(instance);
		instance.registerProvider(ImageIconMessageBodyReader.class);
		FlickrSearchService flickrSearchService = new FlickrSearchService(
				args[0], new LightweightBrowserCache());

		new FlickrClient(flickrSearchService);
	}

	FlickrSearchService flickrSearchService;

	JFrame frame = null;
	JPanel dataPanel = null;
	JTextField textField = null;
	JPanel glassPane = null;

	public FlickrClient(FlickrSearchService flickrSearchService) {
		this.flickrSearchService = flickrSearchService;
		frame = new JFrame();
		frame.setTitle("Flickr Search");
		frame.setLayout(new BorderLayout());
		frame.add(createQueryPanel(), BorderLayout.NORTH);
		frame.add(dataPanel = new JPanel(), BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setGlassPane(glassPane = new JPanel());
		glassPane.setOpaque(true);
		glassPane.setVisible(false);

		frame.pack();
		Dimension preferredSize = frame.getPreferredSize();
		frame.setSize(new Dimension(preferredSize.width + 500,
				preferredSize.height));
		center();
		frame.setVisible(true);
	}

	public void center() {
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolkit.getScreenSize();

		// Calculate the frame location
		int x = (screenSize.width - frame.getWidth()) / 2;
		int y = (screenSize.height - frame.getHeight()) / 2;

		// Set the new frame location
		frame.setLocation(x, y);
	}

	private JPanel createQueryPanel() {
		JPanel queryPanel = new JPanel(new BorderLayout());
		queryPanel.add(new JLabel("Query:"), BorderLayout.WEST);

		textField = new JTextField();
		queryPanel.add(textField, BorderLayout.CENTER);

		JButton searchButton = new JButton("Search");
		queryPanel.add(searchButton, BorderLayout.EAST);

		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					updatePhotos();
				}
			}
		});
		searchButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				updatePhotos();
			}
		});

		return queryPanel;
	}

	private void updatePhotos() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				glassPane.setVisible(true);
				String searchTerm = textField.getText();
				try {
					frame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
					displayPhotos(flickrSearchService.searchPhotos("tags",
							searchTerm));
				} catch (Exception e1) {
					e1.printStackTrace();
				} finally {
					frame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					frame.pack();
					center();
					glassPane.setVisible(false);
					System.out.println(new Date() + " finished search for "
							+ searchTerm);
				}
			}
		});
	}

	private void displayPhotos(FlickrResponse photos)
			throws MalformedURLException, Exception {
		frame.setTitle("Flickr Search for " + textField.getText());
		if (dataPanel != null)
			frame.remove(dataPanel);
		dataPanel = new JPanel();
		dataPanel.setLayout(new GridLayout(2, photos.photo.size() / 2));

		Collections.shuffle(photos.photo);
		
		for (Photo photo : photos.photo) {
			JLabel label = new JLabel(flickrSearchService.getImageIcon(photo));
			label.setBorder(BorderFactory.createTitledBorder(photo.title));
			dataPanel.add(label);
		}
		frame.add(dataPanel, BorderLayout.CENTER);
	}
}
