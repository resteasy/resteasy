package org.jboss.resteasy.examples.flickr;

import static java.lang.String.format;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.commons.lang.time.StopWatch;
import org.jboss.resteasy.examples.resteasy.ImageIconMessageBodyReader;
import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

public class FlickrClient
{

    private final static Logger logger = Logger
            .getLogger(FlickrClient.class);

    public static void main(String args[]) throws Exception
    {
        ResteasyProviderFactory instance = ResteasyProviderFactory
                .getInstance();
        RegisterBuiltin.register(instance);
        instance.registerProvider(ImageIconMessageBodyReader.class);
        FlickrSearchService flickrSearchService = new FlickrSearchService(
                args[0]);

        try
        {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e)
        {

        }
        new FlickrClient(flickrSearchService);
    }

    private FlickrSearchService flickrSearchService;

    private JFrame frame = null;
    private JPanel dataPanel = null;
    private JTextField textField = null;
    private JPanel glassPane = null;
    private ButtonGroup type = null;

    public FlickrClient(FlickrSearchService flickrSearchService)
    {
        this.flickrSearchService = flickrSearchService;
        frame = new JFrame("Flickr Search");
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

    public void center()
    {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();

        // Calculate the frame location
        int x = (screenSize.width - frame.getWidth()) / 2;
        int y = (screenSize.height - frame.getHeight()) / 2;

        // Set the new frame location
        frame.setLocation(x, y);
    }

    private JPanel createQueryPanel()
    {
        JPanel queryPanel = new JPanel(new BorderLayout());
        queryPanel.add(new JLabel("Query:"), BorderLayout.WEST);

        textField = new JTextField();
        queryPanel.add(textField, BorderLayout.CENTER);

        JPanel eastPanel = new JPanel(new FlowLayout());
        JButton searchButton = new JButton("Search");
        this.type = new ButtonGroup();
        addJCheckbox("tags", eastPanel, true);
        addJCheckbox("text", eastPanel, false);
        eastPanel.add(searchButton, BorderLayout.EAST);

        queryPanel.add(eastPanel, BorderLayout.EAST);

        textField.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyReleased(KeyEvent e)
            {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                {
                    updatePhotos();
                }
            }
        });
        searchButton.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                updatePhotos();
            }
        });

        queryPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
                .createEtchedBorder(), BorderFactory.createEmptyBorder(5, 5, 5,
                5)));

        return queryPanel;
    }

    private void addJCheckbox(String string, JPanel panel, boolean selected)
    {
        JRadioButton radioButton = new JRadioButton(string);
        radioButton.setSelected(selected);
        type.add(radioButton);
        panel.add(radioButton);
    }

    private void updatePhotos()
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                StopWatch sw = new StopWatch();
                sw.start();
                glassPane.setVisible(true);
                String searchTerm = textField.getText();
                try
                {
                    frame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                    String searchType = null;
                    for (Enumeration<AbstractButton> elements = type
                            .getElements(); elements.hasMoreElements();)
                    {
                        AbstractButton button = elements.nextElement();
                        if (button.isSelected())
                        {
                            searchType = button.getText();
                            break;
                        }
                    }
                    displayPhotos(flickrSearchService.searchPhotos(searchType,
                            searchTerm));
                } catch (Exception e1)
                {
                    e1.printStackTrace();
                } finally
                {
                    frame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    frame.pack();
                    center();
                    glassPane.setVisible(false);
                    logger.info(format("finished searching for %s in %d ms",
                            searchTerm, sw.getTime()));
                }
            }
        });
    }

    private void displayPhotos(FlickrResponse photos)
            throws MalformedURLException, Exception
    {
        frame.setTitle("Flickr Search for " + textField.getText());
        if (dataPanel != null)
            frame.remove(dataPanel);
        dataPanel = new JPanel();
        dataPanel.setLayout(new GridLayout(2, photos.photos.size() / 2));

        Collections.shuffle(photos.photos);

        for (final Photo photo : photos.photos)
        {
            JPanel photopanel = new JPanel(new BorderLayout());
            JButton button = new JButton(flickrSearchService
                    .getImageIcon(photo));
            button.addMouseListener(new MouseAdapter()
            {
                @Override
                public void mouseClicked(MouseEvent e)
                {
                    BareBonesBrowserLaunch.openURL(photo.getPublicURL());
                }
            });
            photopanel.setBorder(BorderFactory.createTitledBorder(photo.title));
            photopanel.add(button, BorderLayout.CENTER);
            dataPanel.add(photopanel);
        }
        frame.add(dataPanel, BorderLayout.CENTER);
    }
}
