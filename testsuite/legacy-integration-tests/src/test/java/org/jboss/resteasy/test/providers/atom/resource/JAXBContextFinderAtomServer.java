package org.jboss.resteasy.test.providers.atom.resource;

import org.jboss.resteasy.plugins.providers.atom.Entry;
import org.jboss.resteasy.plugins.providers.atom.Feed;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.net.URISyntaxException;

@Path("atom")
public class JAXBContextFinderAtomServer {
    @GET
    @Path("feed")
    @Produces("application/atom+xml")
    public Feed getFeed() throws URISyntaxException {
        Feed feed = new Feed();
        feed.setTitle("My Feed");
        Entry entry = new Entry();
        entry.setTitle("Hello World");
        entry.setAnyOtherJAXBObject(new JAXBContextFinderCustomerAtom("bill"));
        feed.getEntries().add(entry);
        entry = new Entry();
        entry.setTitle("Hello Uranus");
        entry.setAnyOtherJAXBObject(new JAXBContextFinderCustomerAtom("bob"));
        feed.getEntries().add(entry);
        return feed;
    }
}
