package org.jboss.resteasy.test.client.proxy.resource;

import org.jboss.resteasy.test.client.proxy.SubResourceLocatorProxyTest;

public class SubResourceLocatorProxyChapterResource implements SubResourceLocatorProxyTest.Chapter {
    private final int number;

    public SubResourceLocatorProxyChapterResource(final int number) {
        this.number = number;
    }

    public String getTitle() {
        return "Chapter " + number;
    }

    public String getBody() {
        return "This is the content of chapter " + number + ".";
    }
}
