package org.jboss.resteasy.test.client.proxy.resource;

import org.jboss.resteasy.test.client.proxy.SubResourceLocatorProxyTest;

import javax.ws.rs.Path;

@Path("/gulliverstravels")
public class SubResourceLocatorProxyBookResource implements SubResourceLocatorProxyTest.Book {
    public String getTitle() {
        return "Gulliver's Travels";
    }

    @Override
    public SubResourceLocatorProxyTest.Chapter getChapter(int number) {
        return new SubResourceLocatorProxyChapterResource(number);
    }
}
