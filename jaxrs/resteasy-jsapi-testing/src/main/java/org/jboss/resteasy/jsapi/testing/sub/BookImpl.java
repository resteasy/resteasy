package org.jboss.resteasy.jsapi.testing.sub;

import javax.ws.rs.Path;

/**
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */
@Path("/gulliverstravels")
public class BookImpl implements Book {
    public String getTitle() {
        return "Gulliver's Travels";
    }

    @Override
    public Chapter getChapter(int number) {
        return new ChapterImpl(number);
    }
}
