package org.jboss.resteasy.test.providers.atom.resource;

import org.jboss.resteasy.test.providers.atom.AtomProviderTest;
import org.jboss.resteasy.plugins.providers.atom.Content;
import org.jboss.resteasy.plugins.providers.atom.Entry;
import org.jboss.resteasy.plugins.providers.atom.Feed;

import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.POST;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

@Path("atom")
public class AtomProviderResource {
    @GET
    @Path("entry")
    @Produces("application/atom+xml")
    public Entry getEntry() {
        Entry entry = new Entry();
        entry.setTitle("Hello World");
        Content content = new Content();
        content.setJAXBObject(new AtomProviderCustomer("bill"));
        entry.setContent(content);
        return entry;
    }

    @GET
    @Path("feed")
    @Produces("application/atom+xml")
    public Feed getFeed() {
        Feed feed = new Feed();
        feed.getEntries().add(getEntry());
        return feed;
    }

    @GET
    @Path("text/entry")
    @Produces("application/atom+xml")
    public Entry getTextEntry() {
        Entry entry = new Entry();
        entry.setTitle("Hello World");
        Content content = new Content();
        content.setText("<pre>How are you today?\nNotBad!</pre>");
        content.setType(MediaType.TEXT_HTML_TYPE);
        entry.setContent(content);
        return entry;
    }

    @GET
    @Path("text/feed")
    @Produces("application/atom+xml")
    public Feed getTextFeed() {
        Feed feed = new Feed();
        feed.getEntries().add(getTextEntry());
        return feed;
    }

    @POST
    @Path("feed")
    @Consumes("application/atom+xml")
    @Produces("application/atom+xml")
    public Feed postFeed(Feed feed) throws Exception {
        AtomProviderTest.assertFeed(feed);
        return feed;
    }

    @GET
    @Path("xmltype")
    @Produces("application/atom+xml")
    public Entry getXmlType() {
        Entry entry = new Entry();
        entry.setTitle("Hello World");
        Content content = new Content();
        AtomProviderDataCollectionRecord record = new AtomProviderDataCollectionRecord();
        record.setCollectedData("hello world");
        content.setJAXBObject(record);
        entry.setContent(content);
        return entry;
    }
}
