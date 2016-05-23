package org.jboss.resteasy.jsapi.testing.sub;

import javax.ws.rs.Path;

/**
 * @author <a href="mailto:l.weinan@gmail.com">Weinan Li</a>
 */
public class ChapterImpl implements Chapter {
    private final int number;

    public ChapterImpl(int number) {
        this.number = number;
    }

    public String getTitle() {
        return "Chapter " + number;
    }

    public String getBody() {
        return "This is the content of chapter " + number + ".";
    }
}
