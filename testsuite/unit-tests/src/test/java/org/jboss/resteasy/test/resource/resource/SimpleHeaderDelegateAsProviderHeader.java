package org.jboss.resteasy.test.resource.resource;

public class SimpleHeaderDelegateAsProviderHeader {

    private String major;
    private String minor;

    public SimpleHeaderDelegateAsProviderHeader(final String major, final String minor) {
        this.major = major;
        this.minor = minor;
    }

    public String getMajor() {
        return major;
    }

    public String getMinor() {
        return minor;
    }
}
